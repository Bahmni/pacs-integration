package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7;

import ca.uhn.hl7v2.model.v25.segment.ORC;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;

public interface ORCMapper {
    void mapScheduledOrder(ORC orc, OpenMRSOrderDetails orderDetails);
    void mapDiscontinuedOrder(ORC orc, OpenMRSOrderDetails orderDetails);
}
