package digit.service;


import digit.config.DTRConfiguration;
import digit.enrichment.DeathApplicationEnrichment;
import digit.kafka.Producer;
import digit.repository.DeathRegistrationRepository;
import digit.validator.DeathApplicationValidator;
import digit.web.models.DeathApplicationSearchCriteria;
import digit.web.models.DeathRegistrationApplication;
import digit.web.models.DeathRegistrationRequest;
//import digit.web.models.FatherApplicant;
import digit.web.models.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DeathRegistrationService {

    @Autowired
    private DeathApplicationValidator validator;

    @Autowired
    private DeathApplicationEnrichment enrichmentUtil;
    @Autowired
    private DTRConfiguration config;

//    @Autowired
//    private UserService userService;

    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private DeathRegistrationRepository deathRegistrationRepository;

    @Autowired
    private Producer producer;

    public List<DeathRegistrationApplication> registerDtRequest(DeathRegistrationRequest deathRegistrationRequest) {
        // Validate applications
        validator.validateDeathApplication(deathRegistrationRequest);

//         Enrich applications
        enrichmentUtil.enrichDeathApplication(deathRegistrationRequest);

//         Enrich/Upsert user in upon death registration
//        userService.callUserService(deathRegistrationRequest);

//         Initiate workflow for the new application
        workflowService.updateWorkflowStatus(deathRegistrationRequest);
        // Push the application to the topic for persister to listen and persist
        producer.push(config.getCreateTopic(), deathRegistrationRequest);

        // Return the response back to user
        return deathRegistrationRequest.getDeathRegistrationApplications();
    }

    public List<DeathRegistrationApplication> searchDtApplications(RequestInfo requestInfo, DeathApplicationSearchCriteria deathApplicationSearchCriteria) {
        // Fetch applications from database according to the given search criteria
        List<DeathRegistrationApplication> applications = deathRegistrationRepository.getApplications(deathApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        applications.forEach(application -> {
  application.setWorkflow(Workflow.builder().status(workflowService.getCurrentWorkflow(requestInfo, application.getTenantId(), application.getApplicationNumber()).getState().getState()).build());
        });
        return applications;
    }

    public DeathRegistrationApplication updateDtApplication(DeathRegistrationRequest deathRegistrationRequest) {
        // Validate whether the application that is being requested for update indeed exists
        DeathRegistrationApplication existingApplication = validator.validateApplicationExistence(deathRegistrationRequest.getDeathRegistrationApplications().get(0));
        existingApplication.setWorkflow(deathRegistrationRequest.getDeathRegistrationApplications().get(0).getWorkflow());
      log.info(existingApplication.toString());
       deathRegistrationRequest.setDeathRegistrationApplications(Collections.singletonList(existingApplication));

        // Enrich application upon update
        enrichmentUtil.enrichDeathApplicationUponUpdate(deathRegistrationRequest);

        workflowService.updateWorkflowStatus(deathRegistrationRequest);

        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(config.getUpdateTopic(), deathRegistrationRequest);

        return deathRegistrationRequest.getDeathRegistrationApplications().get(0);
    }
}
