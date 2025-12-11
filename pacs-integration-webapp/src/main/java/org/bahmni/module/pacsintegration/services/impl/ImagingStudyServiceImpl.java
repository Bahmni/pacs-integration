package org.bahmni.module.pacsintegration.services.impl;

import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.FhirImagingStudy;
import org.bahmni.module.pacsintegration.atomfeed.mappers.ImagingStudyMapper;
import org.bahmni.module.pacsintegration.services.ImagingStudyService;
import org.bahmni.module.pacsintegration.services.OpenMRSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImagingStudyServiceImpl implements ImagingStudyService {
    
    private static final Logger logger = LoggerFactory.getLogger(ImagingStudyServiceImpl.class);
    
    @Value("${create.imagingstudy.enabled:false}")
    private boolean imagingStudyEnabled;
    
    @Autowired
    private OpenMRSService openMRSService;
    
    @Autowired
    private ImagingStudyMapper imagingStudyMapper;

    @Override
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
            FhirImagingStudy payload = imagingStudyMapper.buildFhirPayload(orderUuid, patientUuid, locationUuid, studyInstanceUID, description);
            openMRSService.createFhirImagingStudy(payload);
            logger.info("Successfully created ImagingStudy for order: {} with StudyInstanceUID: {}", orderUuid, studyInstanceUID);
        } catch (Exception e) {
            logger.error("Failed to create ImagingStudy for order: {}", orderUuid, e);
        }
    }
}
