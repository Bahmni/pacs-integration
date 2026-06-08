package org.bahmni.module.pacsintegration.atomfeed.contract.order;

public class OpenMRSOrderQueryBuilder {

    private static final String BASE_ORDER_DETAILS = "uuid,action,orderNumber,urgency,accessionNumber,commentToFulfiller,dateCreated";
    private static final String ORDER_TYPE = "orderType:(uuid,display,name)";
    private static final String ENCOUNTER = "encounter:(uuid,location:(uuid,display,name,tags:(display),attributes:(uuid,value,attributeType:(uuid,name))))";
    private static final String ATTRIBUTES = "attributes:(uuid,value,attributeType:(name,uuid))";
    private static final String CONCEPT = "concept:(uuid,names,mappings:(conceptMapType:(display),conceptReferenceTerm:(name,code,retired,conceptSource:(uuid,name,hl7Code))))";
    private static final String PATIENT = "patient:(uuid,patientIdentifier:(identifier),person:(gender,age,birthdate,preferredName:(givenName,middleName,familyName)))";
    private static final String PREVIOUS_ORDER_DETAILS = String.format("previousOrder:(%s)", BASE_ORDER_DETAILS);
    private static final String CREATOR = "creator:(uuid,person:(preferredName:(givenName,middleName,familyName)))";

    public static final String ORDER_DETAILS_REPRESENTATION = String.format("custom:(%s,%s,%s,%s,%s,%s,%s,%s)",
            BASE_ORDER_DETAILS, ORDER_TYPE, ENCOUNTER, ATTRIBUTES, CONCEPT, PATIENT, PREVIOUS_ORDER_DETAILS, CREATOR);

    public static final String ORDER_DETAILS_QUERY_PARAM = "v=" + ORDER_DETAILS_REPRESENTATION;
}
