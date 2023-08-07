package digit.repository.querybuilder;

import digit.web.models.DeathApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class DeathApplicationQueryBuilder {


    private static final String BASE_DTR_QUERY = " SELECT dtr.id as bid, dtr.tenantId as bTenantId, dtr.applicationNumber as bApplicationNumber, dtr.deceasedFirstName as bDeceasedFirstName, dtr.deceasedLastname as bDeceasedLastname, dtr.placeOfDeath as bPlaceOfDeath, dtr.timeOfDeath as bTimeOfDeath,dtr.createdBy as bCreatedBy,dtr.lastModifiedBy as bLastModifiedBy,dtr.createdTime as bCreatedTime,dtr.lastModifiedTime as bLastModifiedTime, ";

    private static final String ADDRESS_SELECT_QUERY = " add.id as aId, add.tenantId as aTenantId,  add.latitude as aLatitude, add.longitude as aLongitude, add.addressId as aAddressId, add.addressNumber as aAddressNumber,  add.addressLine1 as aAddressLine1, add.addressLine2 as aAddressLine2, add.landmark as aLandMark, add.city as aCity, add.pinCode as aPinCode, add.detail as aDetail, add.registrationId as aRegistrationId";

    private static final String FROM_TABLES = " FROM eg_dt_registration dtr LEFT JOIN eg_dt_address add ON dtr.id = add.registrationId ";

    private static  final String ORDER_BY_CREATED_TIME = " ORDER BY dtr.createdTime DESC ";

    public String getDeathApplicationSearchQuery(DeathApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_DTR_QUERY);
        query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getTenantId())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" dtr.tenantId= ? ");
            preparedStmtList.add(criteria.getTenantId());
        }
        if(!ObjectUtils.isEmpty(criteria.getStatus())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" dtr.status = ? ");
            preparedStmtList.add(criteria.getStatus());
        }
        if(!CollectionUtils.isEmpty(criteria.getIds())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" dtr.id IN ( ").append(createQuery(criteria.getIds())).append(" ) ");
            addToPreparedStatement(preparedStmtList, criteria.getIds());
        }
        if(!ObjectUtils.isEmpty(criteria.getApplicationNumber())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" dtr.applicationNumber = ? ");
            preparedStmtList.add(criteria.getApplicationNumber());
        }

        // order birth registration applications based on their createdTime in latest first manner
        query.append(ORDER_BY_CREATED_TIME);

        return query.toString();
    }

    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList){
        if(preparedStmtList.isEmpty()){
            query.append(" WHERE ");
        }else{
            query.append(" AND ");
        }
    }

    private String createQuery(List<String> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1)
                builder.append(",");
        }
        return builder.toString();
    }

    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        ids.forEach(id -> {
            preparedStmtList.add(id);
        });
    }
}