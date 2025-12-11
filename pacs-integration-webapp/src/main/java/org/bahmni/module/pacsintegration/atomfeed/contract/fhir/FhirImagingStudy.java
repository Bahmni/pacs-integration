package org.bahmni.module.pacsintegration.atomfeed.contract.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FhirImagingStudy {
    
    private String resourceType = "ImagingStudy";
    private List<FhirIdentifier> identifier;
    private String status;
    private FhirReference subject;
    private List<FhirReference> basedOn;
    private FhirReference location;
    private String description;
    
    public FhirImagingStudy() {
    }
    
    public String getResourceType() {
        return resourceType;
    }
    
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
    
    public List<FhirIdentifier> getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(List<FhirIdentifier> identifier) {
        this.identifier = identifier;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public FhirReference getSubject() {
        return subject;
    }
    
    public void setSubject(FhirReference subject) {
        this.subject = subject;
    }
    
    public List<FhirReference> getBasedOn() {
        return basedOn;
    }
    
    public void setBasedOn(List<FhirReference> basedOn) {
        this.basedOn = basedOn;
    }
    
    public FhirReference getLocation() {
        return location;
    }
    
    public void setLocation(FhirReference location) {
        this.location = location;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
