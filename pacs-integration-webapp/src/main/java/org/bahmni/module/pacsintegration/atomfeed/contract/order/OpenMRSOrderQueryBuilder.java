package org.bahmni.module.pacsintegration.atomfeed.contract.order;

public class OpenMRSOrderQueryBuilder {

    private static final String UUID = "uuid";
    private static final String ORDER_TYPE = "orderType:(uuid,display,name)";
    private static final String ENCOUNTER = "encounter:(uuid,location:(uuid,display,name,tags:(display),attributes:(uuid,value,attributeType:(uuid,name))))";
    private static final String ATTRIBUTES = "attributes:(uuid,value,attributeType:(name,uuid))";
    private static final String CONCEPT = "concept:(uuid,names,mappings:(conceptMapType:(display),conceptReferenceTerm:(name,code,retired,conceptSource:(uuid,name,hl7Code))))";

    public static final String ORDER_DETAILS_REPRESENTATION = String.format("custom:(%s,%s,%s,%s,%s)",
            UUID, ORDER_TYPE, ENCOUNTER, ATTRIBUTES, CONCEPT);

    public static final String ORDER_DETAILS_QUERY_PARAM = "v=" + ORDER_DETAILS_REPRESENTATION;
}
