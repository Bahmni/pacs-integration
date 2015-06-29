package org.bahmni.pacsintegration.atomfeed.contract.encounter;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSOrder {
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

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public Boolean isVoided() {
        if (voided == null)
            return false;
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public String getTestName() {
        return concept.getName().getName();
    }

    public String getUUID() {
        return concept.getUuid();
    }
}
