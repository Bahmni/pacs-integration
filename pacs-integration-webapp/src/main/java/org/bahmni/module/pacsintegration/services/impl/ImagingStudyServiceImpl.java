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

import java.io.IOException;
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

    @Override
    public String createImagingStudy(
            Order order,
            String patientUuid, 
            String locationUuid, 
            String studyInstanceUID,
            String description) throws IOException {
        
        if (StringUtils.isBlank(studyInstanceUID)) {
            throw new IllegalArgumentException("StudyInstanceUID cannot be null or empty");
        }

        FhirImagingStudy payload = imagingStudyMapper.buildFhirPayload(order.getOrderUuid(), patientUuid, locationUuid, studyInstanceUID, description);
        FhirImagingStudy fhirImagingStudy = openMRSService.createFhirImagingStudy(payload);
        String imagingStudyUuid= fhirImagingStudy.getId();

        if (StringUtils.isBlank(imagingStudyUuid)) {
            throw new IOException("Failed to create ImagingStudy - UUID not returned from OpenMRS");
        }
        
        ImagingStudyReference imagingStudyReference = new ImagingStudyReference(
                studyInstanceUID,
                imagingStudyUuid,
                order
        );
        
        imagingStudyReferenceRepository.save(imagingStudyReference);
        return imagingStudyUuid;
    }

    @Override
    public void updateImagingStudyAsAvailable(String studyInstanceUID) throws IOException {
        if (StringUtils.isBlank(studyInstanceUID)) {
            throw new IllegalArgumentException("StudyInstanceUID cannot be null or empty");
        }

        ImagingStudyReference imagingStudyReference = imagingStudyReferenceRepository.findByStudyInstanceUid(studyInstanceUID);
        
        if (imagingStudyReference == null) {
            throw new IOException("ImagingStudyReference not found for StudyInstanceUID: " + studyInstanceUID);
        }
        
        String imagingStudyUuid = imagingStudyReference.getImagingStudyUuid();
        
        if (StringUtils.isBlank(imagingStudyUuid)) {
            throw new IOException("ImagingStudy UUID is null or empty for StudyInstanceUID: " + studyInstanceUID);
        }

        List<JsonPatchOperation> patchOperations = new ArrayList<>();
        patchOperations.add(new JsonPatchOperation("replace", "/status", "available"));
        
        openMRSService.updateFhirImagingStudyStatus(imagingStudyUuid, patchOperations);
        
        logger.info("Successfully updated ImagingStudy status to 'available' for StudyInstanceUID: {} (UUID: {})", 
                studyInstanceUID, imagingStudyUuid);
    }
}
