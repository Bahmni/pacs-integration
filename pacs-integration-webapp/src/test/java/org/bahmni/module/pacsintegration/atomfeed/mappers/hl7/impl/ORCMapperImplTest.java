package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.ORC;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.BaseOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ORCMapperImplTest {

    private ORCMapperImpl orcMapper;
    private ORC orc;

    @Before
    public void setUp() throws HL7Exception {
        orcMapper = new ORCMapperImpl();

        ORM_O01 message = new ORM_O01();
        message.getMSH().getFieldSeparator().setValue("|");
        message.getMSH().getEncodingCharacters().setValue("^~\\&");
        orc = message.getORDER().getORC();
    }

    @Test
    public void shouldMapScheduledOrderWithAllFields() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createScheduledOrderDetails();

        orcMapper.mapScheduledOrder(orc, orderDetails);

        assertEquals("ORD-12345", orc.getPlacerOrderNumber().getEntityIdentifier().getValue());
        assertEquals("ORD-12345", orc.getFillerOrderNumber().getEntityIdentifier().getValue());
        assertEquals(Constants.NEW_ORDER, orc.getOrderControl().getValue());
        assertEquals(Constants.HL7_SCHEDULED_STATUS_CODE, orc.getOrderStatus().getValue());
    }

    @Test
    public void shouldMapScheduledOrderWithStatPriority() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createScheduledOrderDetails();
        orderDetails.setUrgency("STAT");

        orcMapper.mapScheduledOrder(orc, orderDetails);

        assertEquals("S", orc.getQuantityTiming(0).getPriority().getValue());
    }

    @Test
    public void shouldMapScheduledOrderWithRoutinePriority() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createScheduledOrderDetails();
        orderDetails.setUrgency("ROUTINE");

        orcMapper.mapScheduledOrder(orc, orderDetails);

        assertEquals("R", orc.getQuantityTiming(0).getPriority().getValue());
    }

    @Test
    public void shouldMapScheduledOrderWithNullUrgencyAsRoutine() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createScheduledOrderDetails();
        orderDetails.setUrgency(null);

        orcMapper.mapScheduledOrder(orc, orderDetails);

        assertEquals("R", orc.getQuantityTiming(0).getPriority().getValue());
    }

    @Test
    public void shouldMapScheduledOrderWithUrgentAsRoutine() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createScheduledOrderDetails();
        orderDetails.setUrgency("URGENT");

        orcMapper.mapScheduledOrder(orc, orderDetails);

        assertEquals("R", orc.getQuantityTiming(0).getPriority().getValue());
    }

    @Test
    public void shouldMapScheduledOrderWithOrderNumber() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createScheduledOrderDetails();
        orderDetails.setOrderNumber("ORD-99999");

        orcMapper.mapScheduledOrder(orc, orderDetails);

        assertEquals("ORD-99999", orc.getPlacerOrderNumber().getEntityIdentifier().getValue());
        assertEquals("ORD-99999", orc.getFillerOrderNumber().getEntityIdentifier().getValue());
    }

    @Test
    public void shouldMapDiscontinuedOrderWithAllFields() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createDiscontinuedOrderDetails();

        orcMapper.mapDiscontinuedOrder(orc, orderDetails);

        assertEquals("PREV-ORD-123", orc.getPlacerOrderNumber().getEntityIdentifier().getValue());
        assertEquals("PREV-ORD-123", orc.getFillerOrderNumber().getEntityIdentifier().getValue());
        assertEquals(Constants.CANCEL_ORDER, orc.getOrderControl().getValue());
        assertEquals(Constants.HL7_CANCELLED_STATUS_CODE, orc.getOrderStatus().getValue());
    }

    @Test
    public void shouldMapDiscontinuedOrderWithStatPriority() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createDiscontinuedOrderDetails();
        orderDetails.setUrgency("STAT");

        orcMapper.mapDiscontinuedOrder(orc, orderDetails);

        assertEquals("S", orc.getQuantityTiming(0).getPriority().getValue());
    }

    @Test
    public void shouldMapDiscontinuedOrderWithRoutinePriority() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createDiscontinuedOrderDetails();
        orderDetails.setUrgency("ROUTINE");

        orcMapper.mapDiscontinuedOrder(orc, orderDetails);

        assertEquals("R", orc.getQuantityTiming(0).getPriority().getValue());
    }

    @Test
    public void shouldMapDiscontinuedOrderWithPreviousOrderNumber() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createDiscontinuedOrderDetails();
        orderDetails.getPreviousOrder().setOrderNumber("PREV-ORD-999");

        orcMapper.mapDiscontinuedOrder(orc, orderDetails);

        assertEquals("PREV-ORD-999", orc.getPlacerOrderNumber().getEntityIdentifier().getValue());
        assertEquals("PREV-ORD-999", orc.getFillerOrderNumber().getEntityIdentifier().getValue());
    }

    @Test
    public void shouldSetNewOrderControlForScheduledOrder() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createScheduledOrderDetails();

        orcMapper.mapScheduledOrder(orc, orderDetails);

        assertEquals(Constants.NEW_ORDER, orc.getOrderControl().getValue());
    }

    @Test
    public void shouldSetCancelOrderControlForDiscontinuedOrder() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createDiscontinuedOrderDetails();

        orcMapper.mapDiscontinuedOrder(orc, orderDetails);

        assertEquals(Constants.CANCEL_ORDER, orc.getOrderControl().getValue());
    }

    @Test
    public void shouldSetScheduledStatusForScheduledOrder() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createScheduledOrderDetails();

        orcMapper.mapScheduledOrder(orc, orderDetails);

        assertEquals(Constants.HL7_SCHEDULED_STATUS_CODE, orc.getOrderStatus().getValue());
    }

    @Test
    public void shouldSetCancelledStatusForDiscontinuedOrder() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createDiscontinuedOrderDetails();

        orcMapper.mapDiscontinuedOrder(orc, orderDetails);

        assertEquals(Constants.HL7_CANCELLED_STATUS_CODE, orc.getOrderStatus().getValue());
    }

    @Test
    public void shouldMapPlacerAndFillerOrderNumbersToSameValueForScheduledOrder() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createScheduledOrderDetails();

        orcMapper.mapScheduledOrder(orc, orderDetails);

        String placerOrderNumber = orc.getPlacerOrderNumber().getEntityIdentifier().getValue();
        String fillerOrderNumber = orc.getFillerOrderNumber().getEntityIdentifier().getValue();

        assertEquals(placerOrderNumber, fillerOrderNumber);
        assertEquals("ORD-12345", placerOrderNumber);
    }

    @Test
    public void shouldMapPlacerAndFillerOrderNumbersToSameValueForDiscontinuedOrder() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createDiscontinuedOrderDetails();

        orcMapper.mapDiscontinuedOrder(orc, orderDetails);

        String placerOrderNumber = orc.getPlacerOrderNumber().getEntityIdentifier().getValue();
        String fillerOrderNumber = orc.getFillerOrderNumber().getEntityIdentifier().getValue();

        assertEquals(placerOrderNumber, fillerOrderNumber);
        assertEquals("PREV-ORD-123", placerOrderNumber);
    }

    @Test
    public void shouldMapStatUrgencyWithCaseInsensitivity() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createScheduledOrderDetails();
        orderDetails.setUrgency("STAT");

        orcMapper.mapScheduledOrder(orc, orderDetails);

        assertEquals("S", orc.getQuantityTiming(0).getPriority().getValue());
    }

    @Test
    public void shouldTreatEmptyUrgencyAsRoutine() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createScheduledOrderDetails();
        orderDetails.setUrgency("");

        orcMapper.mapScheduledOrder(orc, orderDetails);

        assertEquals("R", orc.getQuantityTiming(0).getPriority().getValue());
    }

    private OpenMRSOrderDetails createScheduledOrderDetails() {
        OpenMRSOrderDetails orderDetails = new OpenMRSOrderDetails();
        orderDetails.setUuid("order-uuid");
        orderDetails.setOrderNumber("ORD-12345");
        orderDetails.setAction("NEW");
        orderDetails.setUrgency("ROUTINE");
        orderDetails.setDateCreated(new Date());
        return orderDetails;
    }

    private OpenMRSOrderDetails createDiscontinuedOrderDetails() {
        OpenMRSOrderDetails orderDetails = new OpenMRSOrderDetails();
        orderDetails.setUuid("order-uuid");
        orderDetails.setOrderNumber("ORD-456");
        orderDetails.setAction("DISCONTINUE");
        orderDetails.setUrgency("ROUTINE");
        orderDetails.setDateCreated(new Date());

        BaseOrderDetails previousOrder = new BaseOrderDetails();
        previousOrder.setUuid("previous-uuid");
        previousOrder.setOrderNumber("PREV-ORD-123");
        previousOrder.setUrgency("ROUTINE");
        previousOrder.setDateCreated(new Date());

        orderDetails.setPreviousOrder(previousOrder);

        return orderDetails;
    }
}
