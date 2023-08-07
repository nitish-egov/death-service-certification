package digit.enrichment;

//import digit.service.UserService;
import digit.util.IdgenUtil;
import digit.util.UserUtil;
import digit.web.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
@Component
@Slf4j
public class DeathApplicationEnrichment {

    @Autowired
    private IdgenUtil idgenUtil;

//    @Autowired
//    private UserService userService;

//    @Autowired
//    private UserUtil userUtils;

    public void enrichDeathApplication(DeathRegistrationRequest deathRegistrationRequest) {
        List<String> deathRegistrationIdList = idgenUtil.getIdList(deathRegistrationRequest.getRequestInfo(), deathRegistrationRequest.getDeathRegistrationApplications().get(0).getTenantId(), "dtr.registrationid", "", deathRegistrationRequest.getDeathRegistrationApplications().size());
        Integer index = 0;
        for(DeathRegistrationApplication application : deathRegistrationRequest.getDeathRegistrationApplications()){
//             Enrich audit details
            AuditDetails auditDetails = AuditDetails.builder().createdBy(deathRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(System.currentTimeMillis()).lastModifiedBy(deathRegistrationRequest.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(System.currentTimeMillis()).build();
            application.setAuditDetails(auditDetails);

            // Enrich UUID
            application.setId(UUID.randomUUID().toString());



//            Enrich application number from IDgen
            application.setApplicationNumber(deathRegistrationIdList.get(index++));

            // Enrich registration Id
            application.getAddressOfDeceased().setRegistrationId(application.getId());

            // Enrich address UUID
            application.getAddressOfDeceased().setId(UUID.randomUUID().toString());



        }
    }

    public void enrichDeathApplicationUponUpdate(DeathRegistrationRequest deathRegistrationRequest) {
        // Enrich lastModifiedTime and lastModifiedBy in case of update
        deathRegistrationRequest.getDeathRegistrationApplications().get(0).getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
        deathRegistrationRequest.getDeathRegistrationApplications().get(0).getAuditDetails().setLastModifiedBy(deathRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
    }

//
//
}