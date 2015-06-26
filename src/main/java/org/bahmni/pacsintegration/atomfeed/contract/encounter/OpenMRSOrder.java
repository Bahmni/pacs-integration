package org.bahmni.pacsintegration.atomfeed.contract.encounter;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSOrder {
    public static final String LAB_ORDER_TYPE = "Lab Order";

    private String uuid;
    private String orderType;
    private Boolean voided;
    private OpenMRSConcept concept;

    public OpenMRSOrder() {
    }

    public OpenMRSOrder(String uuid, String orderType, OpenMRSConcept concept, Boolean voided) {
        this.uuid = uuid;
        this.orderType = orderType;
        this.voided = voided;
        this.concept = concept;
    }

    public String getUuid() {
        return uuid;
    }

    public String getOrderType() {
        return orderType;
    }

    public OpenMRSConcept getConcept() {
        return concept;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public void setConcept(OpenMRSConcept concept) {
        this.concept = concept;
    }

    public boolean isLabOrder() {
        return LAB_ORDER_TYPE.equals(orderType);
    }

    public boolean isLabOrderForPanel() {
        return concept != null && concept.isSet();
    }

    public Boolean isVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public String getLabTestName() {
        if (!isLabOrder())
            return null;
        return concept.getName().getName();
    }

    public String getTestOrPanelUUID() {
        if (!isLabOrder())
            return null;
        return concept.getUuid();
    }
}
