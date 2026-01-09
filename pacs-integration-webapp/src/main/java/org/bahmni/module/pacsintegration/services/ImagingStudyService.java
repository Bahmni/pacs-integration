package org.bahmni.module.pacsintegration.services;

import org.bahmni.module.pacsintegration.model.Order;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface ImagingStudyService {

    String createImagingStudy(
            Order order,
            String patientUuid, 
            String locationUuid, 
            String studyInstanceUID,
            String description) throws IOException;

    void updateImagingStudyStatus(String studyInstanceUID) throws IOException;
}
