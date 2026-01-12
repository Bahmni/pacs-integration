package org.bahmni.module.pacsintegration.atomfeed.contract.fhir;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FhirReference {
    
    private String reference;
    
    public FhirReference() {
    }
    
    public FhirReference(String reference) {
        this.reference = reference;
    }
    
    public String getReference() {
        return reference;
    }
    
    public void setReference(String reference) {
        this.reference = reference;
    }
}
