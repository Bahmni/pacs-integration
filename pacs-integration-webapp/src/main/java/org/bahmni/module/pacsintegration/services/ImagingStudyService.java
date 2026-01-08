package org.bahmni.module.pacsintegration.services;

import org.springframework.stereotype.Service;

@Service
public interface ImagingStudyService {

    String createImagingStudy(
            String orderUuid, 
            String patientUuid, 
            String locationUuid, 
            String studyInstanceUID,
            String description);

    void updateImagingStudyStatus(String studyInstanceUID);
}
