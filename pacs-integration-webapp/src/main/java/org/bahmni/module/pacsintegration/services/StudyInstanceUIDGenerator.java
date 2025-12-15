package org.bahmni.module.pacsintegration.services;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public interface StudyInstanceUIDGenerator {
    String generateStudyInstanceUID(String orderNumber, Date dateCreated);
}
