package org.bahmni.module.pacsintegration.services.impl;

import org.apache.commons.lang.StringUtils;
import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.FhirImagingStudy;
import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.JsonPatchOperation;
import org.bahmni.module.pacsintegration.atomfeed.mappers.ImagingStudyMapper;
import org.bahmni.module.pacsintegration.dto.DicomMetadataDTO;
import org.bahmni.module.pacsintegration.model.ImagingStudyReference;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.repository.ImagingStudyReferenceRepository;
import org.bahmni.module.pacsintegration.services.Dcm4CheeService;
import org.bahmni.module.pacsintegration.services.ImagingStudyService;
import org.bahmni.module.pacsintegration.services.OpenMRSService;
import org.bahmni.module.pacsintegration.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

import java.io.IOException;

@Service
public class ImagingStudyServiceImpl implements ImagingStudyService {

    private static final String DATE_COMPLETION_EXTENSION_URL = "http://fhir.bahmni.org/ext/imaging-study/completion-date";

    private static final Logger logger = LoggerFactory.getLogger(ImagingStudyServiceImpl.class);

    @Autowired
    private OpenMRSService openMRSService;

    @Autowired
    private ImagingStudyMapper imagingStudyMapper;

    @Autowired
    private ImagingStudyReferenceRepository imagingStudyReferenceRepository;

    @Autowired
    private Dcm4CheeService dcm4CheeService;

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

        DicomMetadataDTO[] metadataArray = dcm4CheeService.fetchStudyMetadata(studyInstanceUID);
        Date studyDateTime = getStudyDateTime(studyInstanceUID, metadataArray);

        List<JsonPatchOperation> patchOperations = new ArrayList<>();
        patchOperations.add(new JsonPatchOperation("replace", "/status", "available"));

        if (studyDateTime != null) {
            Map<String, Object> extension = createDateCompletedExtension(studyDateTime);
            patchOperations.add(new JsonPatchOperation("add", "/extension", Arrays.asList(extension)));
        }

        openMRSService.updateFhirImagingStudyStatus(imagingStudyUuid, patchOperations);
        
        logger.info("Successfully updated ImagingStudy status to 'available' for StudyInstanceUID: {} (UUID: {})", 
                studyInstanceUID, imagingStudyUuid);
    }

    private static Date getStudyDateTime(String studyInstanceUID, DicomMetadataDTO[] metadataArray) {
        if (metadataArray == null || metadataArray.length == 0) {
            return null;
        }

        DicomMetadataDTO metadata = metadataArray[0];
        String acquisitionDateTime = metadata.getAcquisitionDateTime();
        String acquisitionDate = metadata.getAcquisitionDate();
        String acquisitionTime = metadata.getAcquisitionTime();
        String utcOffset = metadata.getTimezoneOffsetFromUTC();

        if (StringUtils.isNotBlank(acquisitionDateTime)) {
            logger.info("Using acquisition date time(0008002A) as : {} for study {}", acquisitionDateTime, studyInstanceUID);
            return DateUtils.parseDicomDateTimeWithOffset(acquisitionDateTime);
        }

        if (StringUtils.isNotBlank(acquisitionDate) && StringUtils.isNotBlank(acquisitionTime) && StringUtils.isNotBlank(utcOffset)) {
            logger.info("Using datetime with UTC offset(00080201) for study {}", studyInstanceUID);
            return DateUtils.parseDicomDateTimeWithOffset(acquisitionDate + acquisitionTime + utcOffset);
        }

        if (StringUtils.isNotBlank(acquisitionDate) && StringUtils.isNotBlank(acquisitionTime)) {
            logger.info("Using local datetime (no UTC offset) for study {}", studyInstanceUID);
            return DateUtils.combineDicomDateTime(acquisitionDate, acquisitionTime);
        }
        return null;
    }

    private Map<String, Object> createDateCompletedExtension(Date studyDateTime) {
        Map<String, Object> extension = new HashMap<>();
        extension.put("url", DATE_COMPLETION_EXTENSION_URL);
        extension.put("valueDateTime", DateUtils.formatFhirDateTime(studyDateTime));
        return extension;
    }
}
