package org.bahmni.module.pacsintegration.services.impl;

import org.apache.commons.lang.StringUtils;
import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.FhirImagingStudy;
import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.JsonPatchOperation;
import org.bahmni.module.pacsintegration.atomfeed.mappers.ImagingStudyMapper;
import org.bahmni.module.pacsintegration.model.ImagingStudyReference;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.repository.ImagingStudyReferenceRepository;
import org.bahmni.module.pacsintegration.repository.OrderRepository;
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

    @Autowired
    private ImagingStudyReferenceRepository imagingStudyReferenceRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public String createImagingStudy(
            String orderUuid, 
            String patientUuid, 
            String locationUuid, 
            String studyInstanceUID,
            String description) {
        
        if (StringUtils.isBlank(studyInstanceUID)) {
            logger.warn("Cannot create ImagingStudy for order {} - StudyInstanceUID is null or empty", orderUuid);
            return null;
        }

        try {
            FhirImagingStudy payload = imagingStudyMapper.buildFhirPayload(orderUuid, patientUuid, locationUuid, studyInstanceUID, description);
            String imagingStudyUuid = openMRSService.createFhirImagingStudy(payload);
            
            if (StringUtils.isBlank(imagingStudyUuid)) {
                logger.error("Failed to get ImagingStudy UUID from OpenMRS for order: {}", orderUuid);
                return null;
            }

            Order order = orderRepository.findByOrderUuid(orderUuid);
            if (order == null) {
                logger.error("Order not found with UUID: {}. Cannot save ImagingStudyReference", orderUuid);
                return imagingStudyUuid;
            }
            
            ImagingStudyReference imagingStudyReference = new ImagingStudyReference(
                    studyInstanceUID,
                    imagingStudyUuid,
                    order
            );
            
            imagingStudyReferenceRepository.save(imagingStudyReference);
            return imagingStudyUuid;
        } catch (Exception e) {
            logger.error("Failed to create ImagingStudy for order: {}", orderUuid, e);
            return null;
        }
    }

    @Override
    public void updateImagingStudyStatus(String studyInstanceUID) {
        if (StringUtils.isBlank(studyInstanceUID)) {
            logger.warn("Cannot update ImagingStudy status - StudyInstanceUID is null or empty");
            return;
        }

        try {
            ImagingStudyReference imagingStudyReference = imagingStudyReferenceRepository.findByStudyInstanceUid(studyInstanceUID);
            
            if (imagingStudyReference == null) {
                logger.warn("ImagingStudyReference not found for StudyInstanceUID: {}. Cannot update status.", studyInstanceUID);
                return;
            }
            
            String imagingStudyUuid = imagingStudyReference.getImagingStudyUuid();
            
            if (StringUtils.isBlank(imagingStudyUuid)) {
                logger.error("ImagingStudy UUID is null or empty for StudyInstanceUID: {}", studyInstanceUID);
                return;
            }

            List<JsonPatchOperation> patchOperations = new ArrayList<>();
            patchOperations.add(new JsonPatchOperation("replace", "/status", "available"));
            
            openMRSService.updateFhirImagingStudyStatus(imagingStudyUuid, patchOperations);
            
            logger.info("Successfully updated ImagingStudy status to 'available' for StudyInstanceUID: {} (UUID: {})", 
                    studyInstanceUID, imagingStudyUuid);
            
        } catch (Exception e) {
            logger.error("Failed to update ImagingStudy status for StudyInstanceUID: {}", studyInstanceUID, e);
        }
    }
}
