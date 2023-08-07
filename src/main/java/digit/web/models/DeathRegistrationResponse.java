package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * Contract class to send response. Array of  items are used in case of search results or response for create, whereas single  item is used for update
 */
@ApiModel(description = "Contract class to send response. Array of  items are used in case of search results or response for create, whereas single  item is used for update")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2023-07-28T12:28:06.435+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeathRegistrationResponse   {
        @JsonProperty("ResponseInfo")
        private ResponseInfo responseInfo = null;

        @JsonProperty("DeathRegistrationApplications")
        @Valid
        private List<DeathRegistrationApplication> deathRegistrationApplications = null;


        public DeathRegistrationResponse addDeathRegistrationApplicationsItem(DeathRegistrationApplication deathRegistrationApplicationsItem) {
            if (this.deathRegistrationApplications == null) {
            this.deathRegistrationApplications = new ArrayList<>();
            }
        this.deathRegistrationApplications.add(deathRegistrationApplicationsItem);
        return this;
        }

}

