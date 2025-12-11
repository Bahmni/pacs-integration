package org.bahmni.module.pacsintegration.atomfeed.mappers;

import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.FhirIdentifier;
import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.FhirImagingStudy;
import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.FhirReference;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ImagingStudyMapper {

    public FhirImagingStudy buildFhirPayload(
            String orderUuid, 
            String patientUuid, 
            String locationUuid,
            String studyInstanceUID,
            String description) throws Exception {
        
        FhirImagingStudy imagingStudy = new FhirImagingStudy();

        imagingStudy.setResourceType("ImagingStudy");

        FhirIdentifier identifier = new FhirIdentifier("urn:dicom:uid", studyInstanceUID);
        imagingStudy.setIdentifier(Arrays.asList(identifier));

        imagingStudy.setStatus("unknown");

        FhirReference subject = new FhirReference("Patient/" + patientUuid);
        imagingStudy.setSubject(subject);

        FhirReference basedOnReference = new FhirReference("ServiceRequest/" + orderUuid);
        imagingStudy.setBasedOn(Arrays.asList(basedOnReference));

        if (locationUuid != null && !locationUuid.trim().isEmpty()) {
            FhirReference location = new FhirReference("Location/" + locationUuid);
            imagingStudy.setLocation(location);
        }

        if (description != null && !description.trim().isEmpty()) {
            imagingStudy.setDescription(description);
        }

        return imagingStudy;
    }
}
