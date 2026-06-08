package org.bahmni.module.pacsintegration.atomfeed.mappers;

import org.apache.commons.lang.StringUtils;
import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.FhirIdentifier;
import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.FhirImagingStudy;
import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.FhirReference;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ImagingStudyMapper {
    private static final String RESOURCE_TYPE = "ImagingStudy";
    private static final String IDENTIFIER_SYSTEM = "urn:dicom:uid";
    private static final String STATUS = "unknown";
    private static final String PATIENT_REFERENCE_PREFIX = "Patient/";
    private static final String SERVICE_REQUEST_REFERENCE_PREFIX = "ServiceRequest/";
    private static final String LOCATION_REFERENCE_PREFIX = "Location/";

    public FhirImagingStudy buildFhirPayload(
            String orderUuid, 
            String patientUuid, 
            String locationUuid,
            String studyInstanceUID,
            String description) {
        
        FhirImagingStudy imagingStudy = new FhirImagingStudy();

        imagingStudy.setResourceType(RESOURCE_TYPE);

        FhirIdentifier identifier = new FhirIdentifier(IDENTIFIER_SYSTEM, studyInstanceUID);
        imagingStudy.setIdentifier(Collections.singletonList(identifier));

        imagingStudy.setStatus(STATUS);

        FhirReference subject = new FhirReference(PATIENT_REFERENCE_PREFIX + patientUuid);
        imagingStudy.setSubject(subject);

        FhirReference basedOnReference = new FhirReference(SERVICE_REQUEST_REFERENCE_PREFIX + orderUuid);
        imagingStudy.setBasedOn(Collections.singletonList(basedOnReference));

        if (StringUtils.isNotBlank(locationUuid)) {
            FhirReference location = new FhirReference(LOCATION_REFERENCE_PREFIX + locationUuid);
            imagingStudy.setLocation(location);
        }

        if (StringUtils.isNotBlank(description)) {
            imagingStudy.setDescription(description);
        }

        return imagingStudy;
    }
}
