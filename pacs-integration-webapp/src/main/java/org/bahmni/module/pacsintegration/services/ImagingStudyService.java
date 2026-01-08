package org.bahmni.module.pacsintegration.services;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface ImagingStudyService {

    String createImagingStudy(
            String orderUuid, 
            String patientUuid, 
            String locationUuid, 
            String studyInstanceUID,
            String description) throws IOException;

    void updateImagingStudyStatus(String studyInstanceUID) throws IOException;
}
