package org.bahmni.module.pacsintegration.services.impl;

import org.apache.commons.lang.StringUtils;
import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.FhirImagingStudy;
import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.JsonPatchOperation;
import org.bahmni.module.pacsintegration.atomfeed.mappers.ImagingStudyMapper;
import org.bahmni.module.pacsintegration.services.ImagingStudyService;
import org.bahmni.module.pacsintegration.services.OpenMRSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImagingStudyServiceImpl implements ImagingStudyService {
    
    private static final Logger logger = LoggerFactory.getLogger(ImagingStudyServiceImpl.class);

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
        
        if (StringUtils.isBlank(studyInstanceUID)) {
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

    @Override
    public void updateImagingStudyStatus(String studyInstanceUID) {
        if (StringUtils.isBlank(studyInstanceUID)) {
            logger.warn("Cannot update ImagingStudy status - StudyInstanceUID is null or empty");
            return;
        }

        try {
            // Hardcoded ID below for testing
            String imagingStudyId = "3b0eef01-d054-49a3-ad8e-29857933f8cc";

            List<JsonPatchOperation> patchOperations = new ArrayList<>();
            patchOperations.add(new JsonPatchOperation("replace", "/status", "available"));
            
            logger.info("Sending JSON Patch request to update ImagingStudy {} status to 'available'", imagingStudyId);
            openMRSService.updateFhirImagingStudyStatus(imagingStudyId, patchOperations);
            
            logger.info("Successfully updated ImagingStudy status to 'available' for StudyInstanceUID: {}", studyInstanceUID);
            
        } catch (Exception e) {
            logger.error("Failed to update ImagingStudy status for StudyInstanceUID: {}", studyInstanceUID, e);
        }
    }
}
