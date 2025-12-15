package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7;

import ca.uhn.hl7v2.model.v25.segment.MSH;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;

public interface MessageHeaderMapper {
    void map(MSH messageHeader, OpenMRSOrderDetails orderDetails);
}
