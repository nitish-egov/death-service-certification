package digit.validator;

import digit.repository.DeathRegistrationRepository;
import digit.web.models.DeathApplicationSearchCriteria;
import digit.web.models.DeathRegistrationApplication;
import digit.web.models.DeathRegistrationRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class DeathApplicationValidator {

    @Autowired
    private DeathRegistrationRepository repository;

    public void validateDeathApplication(DeathRegistrationRequest deathRegistrationRequest) {
        if(deathRegistrationRequest.getDeathRegistrationApplications()!=null) {
            deathRegistrationRequest.getDeathRegistrationApplications().forEach(application -> {
                if (ObjectUtils.isEmpty(application.getTenantId()))
                    throw new CustomException("EG_DT_APP_ERR", "tenantId is mandatory for creating death registration applications");
            });
        }
    }

    public DeathRegistrationApplication validateApplicationExistence(DeathRegistrationApplication deathRegistrationApplication) {
        List<DeathRegistrationApplication> applications = repository.getApplications(DeathApplicationSearchCriteria.builder().applicationNumber(deathRegistrationApplication.getApplicationNumber()).build());

        if (applications.isEmpty()) {
            // Handle the case when the list is empty
            throw new CustomException("EG_DT_APP_ERR", "No matching application found with the provided application number");
            // Or, you can return null or take any other appropriate action.
        }

        return applications.get(0);
    }
}