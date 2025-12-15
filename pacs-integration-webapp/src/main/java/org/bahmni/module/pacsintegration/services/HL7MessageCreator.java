package org.bahmni.module.pacsintegration.services;

import ca.uhn.hl7v2.model.AbstractMessage;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;

public interface HL7MessageCreator {
    AbstractMessage createHL7Message(OpenMRSOrderDetails orderDetails);
}
