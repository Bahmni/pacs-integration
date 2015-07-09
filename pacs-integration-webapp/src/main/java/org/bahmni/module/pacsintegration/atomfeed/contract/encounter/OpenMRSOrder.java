package org.bahmni.module.pacsintegration.atomfeed.contract.encounter;

import org.bahmni.module.pacsintegration.atomfeed.client.Constants;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSOrder {
    private String uuid;
    private String orderType;
    private Boolean voided;
    private OpenMRSConcept concept;
    private String orderNumber;

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

    public String getConceptUUID() {
        return concept.getUuid();
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OpenMRSConceptMapping getPacsConceptSource() {
        for (OpenMRSConceptMapping mapping : concept.getMappings()){
            if(mapping.getSource().equals(Constants.PACS_CONCEPT_SOURCE_NAME))
                return mapping;
        }
        return null;
    }
}
