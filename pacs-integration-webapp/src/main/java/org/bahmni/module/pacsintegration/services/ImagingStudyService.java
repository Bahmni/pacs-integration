package org.bahmni.module.pacsintegration.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ImagingStudyService {
    
    private static final Logger logger = LoggerFactory.getLogger(ImagingStudyService.class);
    
    @Value("${create.imagingstudy.enabled}")
    private boolean imagingStudyEnabled;
    
    @Autowired
    private OpenMRSService openMRSService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void createImagingStudy(
            String orderUuid, 
            String patientUuid, 
            String locationUuid, 
            String studyInstanceUID,
            String description) {
        
        if (!imagingStudyEnabled) {
            logger.debug("ImagingStudy creation is disabled. Skipping for order: {}", orderUuid);
            return;
        }
        
        if (studyInstanceUID == null || studyInstanceUID.trim().isEmpty()) {
            logger.warn("Cannot create ImagingStudy for order {} - StudyInstanceUID is null or empty", orderUuid);
            return;
        }
        
        try {
            String payload = buildImagingStudyPayload(orderUuid, patientUuid, locationUuid, studyInstanceUID, description);
            openMRSService.createFhirImagingStudy(payload);
            logger.info("Successfully created ImagingStudy for order: {} with StudyInstanceUID: {}", orderUuid, studyInstanceUID);
        } catch (Exception e) {
            logger.error("Failed to create ImagingStudy for order: {}", orderUuid, e);
        }
    }
    

    private String buildImagingStudyPayload(
            String orderUuid, 
            String patientUuid, 
            String locationUuid,
            String studyInstanceUID,
            String description) throws Exception {
        
        Map<String, Object> imagingStudy = new HashMap();
        imagingStudy.put("resourceType", "ImagingStudy");

        Map<String, Object> identifier = new HashMap();
        identifier.put("system", "urn:dicom:uid");
        identifier.put("value", studyInstanceUID);
        imagingStudy.put("identifier", new Object[]{identifier});

        imagingStudy.put("status", "unknown");

        Map<String, Object> subject = new HashMap();
        subject.put("reference", "Patient/" + patientUuid);
        imagingStudy.put("subject", subject);

        Map<String, Object> basedOn = new HashMap();
        basedOn.put("reference", "ServiceRequest/" + orderUuid);
        imagingStudy.put("basedOn", new Object[]{basedOn});

        if (locationUuid != null && !locationUuid.trim().isEmpty()) {
            Map<String, Object> location = new HashMap();
            location.put("reference", "Location/" + locationUuid);
            imagingStudy.put("location", location);
        }

        if (description != null && !description.trim().isEmpty()) {
            imagingStudy.put("description", description);
        }
        
        return objectMapper.writeValueAsString(imagingStudy);
    }
}
