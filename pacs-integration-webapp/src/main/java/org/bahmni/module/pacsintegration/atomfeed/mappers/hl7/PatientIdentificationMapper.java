package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7;

import ca.uhn.hl7v2.model.v25.segment.PID;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;

public interface PatientIdentificationMapper {
    void map(PID pid, OpenMRSOrderDetails orderDetails);
}
