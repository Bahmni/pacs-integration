package org.bahmni.module.pacsintegration.atomfeed.contract.patient;

public class CareContext {
    private String careContextType;
    private String careContextName;
    private Integer careContextReference;

    public Integer getCareContextReference() {
        return careContextReference;
    }

    public void setCareContextReference(Integer careContextReference) {
        this.careContextReference = careContextReference;
    }

    public String getCareContextType() {
        return careContextType;
    }

    public void setCareContextType(String careContextType) {
        this.careContextType = careContextType;
    }

    public String getCareContextName() {
        return careContextName;
    }

    public void setCareContextName(String careContextName) {
        this.careContextName = careContextName;
    }


}
