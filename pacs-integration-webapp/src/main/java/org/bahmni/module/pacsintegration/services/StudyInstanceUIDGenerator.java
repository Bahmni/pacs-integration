package org.bahmni.module.pacsintegration.services;

import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.springframework.stereotype.Service;

@Service
public interface StudyInstanceUIDGenerator {
    String generateStudyInstanceUID(String orderNumber);
}
