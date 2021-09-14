package org.bahmni.module.pacsintegration.atomfeed.contract.patient;

public class CareContext {
    private String Type;
    private String Display;
    private Integer ReferenceNumber;

    public Integer getReferenceNumber() {
        return ReferenceNumber;
    }

    public void setReferenceNumber(Integer referenceNumber) {
        this.ReferenceNumber = referenceNumber;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public String getDisplay() {
        return Display;
    }

    public void setDisplay(String display) {
        this.Display = display;
    }


}
