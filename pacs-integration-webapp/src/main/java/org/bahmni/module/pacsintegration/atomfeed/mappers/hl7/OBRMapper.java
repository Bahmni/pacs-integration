package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7;

import ca.uhn.hl7v2.model.v25.segment.OBR;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;

public interface OBRMapper {
    void map(OBR obr, OpenMRSOrderDetails orderDetails);
}
