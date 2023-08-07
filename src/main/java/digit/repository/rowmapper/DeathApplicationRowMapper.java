package digit.repository.rowmapper;

import digit.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import digit.web.models.AuditDetails;

@Component
public class DeathApplicationRowMapper implements ResultSetExtractor<List<DeathRegistrationApplication>> {


    public List<DeathRegistrationApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,DeathRegistrationApplication> deathRegistrationApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            String uuid = rs.getString("bApplicationNumber");
            DeathRegistrationApplication deathRegistrationApplication = deathRegistrationApplicationMap.get(uuid);

            if(deathRegistrationApplication == null) {

                Long lastModifiedTime = rs.getLong("blastModifiedTime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }


//                Applicant father = Applicant.builder().id(rs.getString("bfatherid")).build();
//                Applicant mother = Applicant.builder().id(rs.getString("bmotherid")).build();

                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("bCreatedBy"))
                        .createdTime(rs.getLong("bCreatedTime"))
                        .lastModifiedBy(rs.getString("bLastModifiedBy"))
                        .lastModifiedTime(rs.getLong("bLastModifiedTime"))
                        .build();

                deathRegistrationApplication = DeathRegistrationApplication.builder()
                        .applicationNumber(rs.getString("bApplicationNumber"))
                        .tenantId(rs.getString("bTenantId"))
                        .id(rs.getString("bId"))
                        .deceasedFirstName(rs.getString("bDeceasedFirstName"))
                        .deceasedLastName(rs.getString("bDeceasedLastName"))
//                        .fatherOfApplicant(father)
//                        .motherOfApplicant(mother)
//                        .doctorName(rs.getString("bdoctorname"))
//                        .hospitalName(rs.getString("bhospitalname"))
                        .placeOfDeath(rs.getString("bPlaceOfDeath"))
                        .timeOfDeath(rs.getInt("bTimeOfDeath"))
                        .auditDetails(auditdetails)
                        .build();
            }
            addChildrenToProperty(rs, deathRegistrationApplication);
            deathRegistrationApplicationMap.put(uuid, deathRegistrationApplication);
        }
        return new ArrayList<>(deathRegistrationApplicationMap.values());
    }

    private void addChildrenToProperty(ResultSet rs, DeathRegistrationApplication deathRegistrationApplication)
            throws SQLException {
        addAddressToApplication(rs, deathRegistrationApplication);
    }

    private void addAddressToApplication(ResultSet rs, DeathRegistrationApplication deathRegistrationApplication) throws SQLException {
//        AuditDetails auditdetails = deathRegistrationApplication.getAuditDetails();
        AuditDetails auditdetails = AuditDetails.builder()
                .createdBy(rs.getString("bCreatedBy"))
                .createdTime(rs.getLong("bCreatedTime"))
                .lastModifiedBy(rs.getString("blastModifiedBy"))
                .lastModifiedTime(rs.getLong("blastModifiedTime"))
                .build();

        Address address = Address.builder()
                .id(rs.getString("aId"))
                .tenantId(rs.getString("atenantId"))
//                .doorNo(rs.getString("adoorno"))
                .latitude(rs.getDouble("aLatitude"))
                .longitude(rs.getDouble("aLongitude"))
//                .buildingName(rs.getString("abuildingname"))
                .addressId(rs.getString("aAddressId"))
                .addressNumber(rs.getString("aAddressNumber"))
//                .type(rs.getString("atype"))
                .addressLine1(rs.getString("aAddressLine1"))
                .addressLine2(rs.getString("aAddressLine2"))
                .landmark(rs.getString("aLandmark"))
//                .street(rs.getString("astreet"))
                .city(rs.getString("aCity"))
                .pincode(rs.getString("aPinCode"))
                .detail(rs.getString("aDetail"))
                .registrationId(rs.getString("aRegistrationId"))
                .auditDetails(auditdetails)
                .build();

        deathRegistrationApplication.setAddressOfDeceased(address);

    }

}
