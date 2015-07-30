package org.bahmni.module.pacsintegration.atomfeed.contract.encounter;

import org.bahmni.module.pacsintegration.atomfeed.client.Constants;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSOrder {
    public static final String ACTION_NEW = "NEW";
    public static final String ACTION_DISCONTINUE = "DISCONTINUE";

    private String action;
    private String uuid;
    private String orderType;
    private Boolean voided;
    private OpenMRSConcept concept;
    private String orderNumber;
    private String previousOrderUuid;
    private String commentToFulfiller;

    public OpenMRSOrder() {
        this.action = ACTION_NEW;
    }

    public OpenMRSOrder(String uuid, String orderType, OpenMRSConcept concept, Boolean voided, String action, String previousOrderUuid) {
        this.uuid = uuid;
        this.orderType = orderType;
        this.voided = voided;
        this.concept = concept;
        this.action = action != null ? action : ACTION_NEW;
        this.previousOrderUuid = previousOrderUuid;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPreviousOrderUuid() {
        return previousOrderUuid;
    }

    public void setPreviousOrderUuid(String previousOrderUuid) {
        this.previousOrderUuid = previousOrderUuid;
    }

    public boolean isDiscontinued() {
        return ACTION_DISCONTINUE.equals(this.action);
    }

    public boolean isNew() {
        return ACTION_NEW.equals(this.action);
    }

    public String getCommentToFulfiller() {
        return commentToFulfiller;
    }

    public void setCommentToFulfiller(String commentToFulfiller) {
        this.commentToFulfiller = commentToFulfiller;
    }

}
