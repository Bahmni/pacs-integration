package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.segment.ORC;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.ORCMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.Constants.CANCEL_ORDER;
import static org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.Constants.HL7_CANCELLED_STATUS_CODE;
import static org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.Constants.HL7_SCHEDULED_STATUS_CODE;
import static org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.Constants.NEW_ORDER;

@Component
public class ORCMapperImpl implements ORCMapper {

    private static final Logger logger = LoggerFactory.getLogger(ORCMapperImpl.class);

    @Override
    public void mapScheduledOrder(ORC orc, OpenMRSOrderDetails orderDetails) {
        try {
            logger.debug("Mapping ORC segment for scheduled order: {}", orderDetails.getOrderNumber());
            mapPriorityFromUrgency(orc, orderDetails);
            mapEnteredBy(orc);
            orc.getPlacerOrderNumber().getEntityIdentifier().setValue(orderDetails.getOrderNumber());
            orc.getFillerOrderNumber().getEntityIdentifier().setValue(orderDetails.getOrderNumber());
            orc.getOrderControl().setValue(NEW_ORDER);
            orc.getOrderStatus().setValue(HL7_SCHEDULED_STATUS_CODE);
            logger.info("Successfully mapped ORC segment for scheduled order: {}", orderDetails.getOrderNumber());
        } catch (HL7Exception e) {
            logger.error("Error mapping ORC segment for order: {}", orderDetails.getOrderNumber(), e);
            throw new RuntimeException("Failed to map common order segment", e);
        }
    }

    @Override
    public void mapDiscontinuedOrder(ORC orc, OpenMRSOrderDetails orderDetails) {
        try {
            logger.debug("Mapping ORC segment for cancelled order: {}", orderDetails.getOrderNumber());
            mapPriorityFromUrgency(orc, orderDetails);
            orc.getPlacerOrderNumber().getEntityIdentifier().setValue(orderDetails.getPreviousOrder().getOrderNumber());
            orc.getFillerOrderNumber().getEntityIdentifier().setValue(orderDetails.getPreviousOrder().getOrderNumber());
            orc.getOrderControl().setValue(CANCEL_ORDER);
            orc.getOrderStatus().setValue(HL7_CANCELLED_STATUS_CODE);
            logger.info("Successfully mapped ORC segment for cancelled order: {}", orderDetails.getOrderNumber());
        } catch (HL7Exception e) {
            logger.error("Error mapping ORC segment for order: {}", orderDetails.getOrderNumber(), e);
            throw new RuntimeException("Failed to map common order segment", e);
        }
    }

    private void mapPriorityFromUrgency(ORC orc, OpenMRSOrderDetails orderDetails) throws HL7Exception {
        String priority = "STAT".equals(orderDetails.getUrgency()) ? "S" : "R";
        orc.getQuantityTiming(0).getPriority().parse(priority);
    }

    private void mapEnteredBy(ORC orc) throws DataTypeException {
        orc.getEnteredBy(0).getGivenName().setValue("Bahmni EMR");
    }
}
