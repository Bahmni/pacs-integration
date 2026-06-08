package org.bahmni.module.pacsintegration.services.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.v25.group.ORM_O01_ORDER;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.ORC;
import ca.uhn.hl7v2.model.v25.segment.PID;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.BaseOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.MessageHeaderMapper;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.OBRMapper;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.ORCMapper;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.PatientIdentificationMapper;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.ZDSMapper;
import org.bahmni.module.pacsintegration.exception.HL7MessageException;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.repository.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HL7MessageCreatorImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MessageHeaderMapper messageHeaderMapper;

    @Mock
    private PatientIdentificationMapper patientIdentificationMapper;

    @Mock
    private ORCMapper orcMapper;

    @Mock
    private OBRMapper obrMapper;

    @Mock
    private ZDSMapper zdsMapper;

    private HL7MessageCreatorImpl hl7MessageCreator;

    @Before
    public void setUp() {
        hl7MessageCreator = new HL7MessageCreatorImpl(
                orderRepository,
                messageHeaderMapper,
                patientIdentificationMapper,
                orcMapper,
                obrMapper,
                zdsMapper
        );
    }

    @Test
    public void shouldCreateHL7MessageForScheduledOrder() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createScheduledOrderDetails();

        AbstractMessage result = hl7MessageCreator.createHL7Message(orderDetails);

        assertNotNull(result);
        assertTrue(result instanceof ORM_O01);
        ORM_O01 message = (ORM_O01) result;

        verify(orcMapper).mapScheduledOrder(any(ORC.class), eq(orderDetails));
        verify(zdsMapper).mapStudyInstanceUID(any(ORM_O01.class), eq("ORD-123"), eq(orderDetails.getDateCreated()));
        verify(messageHeaderMapper).map(any(MSH.class), eq(orderDetails));
        verify(patientIdentificationMapper).map(any(PID.class), eq(orderDetails));
        verify(obrMapper).map(any(OBR.class), eq(orderDetails));
        verify(orcMapper, never()).mapDiscontinuedOrder(any(ORC.class), any(OpenMRSOrderDetails.class));
    }

    @Test
    public void shouldCreateHL7MessageForDiscontinuedOrder() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createDiscontinuedOrderDetails();
        Order previousOrder = new Order();
        previousOrder.setOrderUuid("previous-uuid");

        when(orderRepository.findByOrderUuid("previous-uuid")).thenReturn(previousOrder);

        AbstractMessage result = hl7MessageCreator.createHL7Message(orderDetails);

        assertNotNull(result);
        assertTrue(result instanceof ORM_O01);
        ORM_O01 message = (ORM_O01) result;

        verify(orcMapper).mapDiscontinuedOrder(any(ORC.class), eq(orderDetails));
        verify(zdsMapper).mapStudyInstanceUID(any(ORM_O01.class), eq("PREV-ORD-123"), eq(orderDetails.getPreviousOrder().getDateCreated()));
        verify(messageHeaderMapper).map(any(MSH.class), eq(orderDetails));
        verify(patientIdentificationMapper).map(any(PID.class), eq(orderDetails));
        verify(obrMapper).map(any(OBR.class), eq(orderDetails));
        verify(orcMapper, never()).mapScheduledOrder(any(ORC.class), any(OpenMRSOrderDetails.class));
        verify(orderRepository).findByOrderUuid("previous-uuid");
    }

    @Test(expected = HL7MessageException.class)
    public void shouldThrowExceptionWhenPreviousOrderNotFound() {
        OpenMRSOrderDetails orderDetails = createDiscontinuedOrderDetails();

        when(orderRepository.findByOrderUuid("previous-uuid")).thenReturn(null);

        hl7MessageCreator.createHL7Message(orderDetails);
    }

    @Test
    public void shouldIncludeOrderNumberInExceptionMessageWhenPreviousOrderNotFound() {
        OpenMRSOrderDetails orderDetails = createDiscontinuedOrderDetails();
        when(orderRepository.findByOrderUuid("previous-uuid")).thenReturn(null);

        try {
            hl7MessageCreator.createHL7Message(orderDetails);
            fail("Expected HL7MessageException to be thrown");
        } catch (HL7MessageException e) {
            assertTrue(e.getMessage().contains("Unable to Cancel the Order"));
            assertTrue(e.getMessage().contains("ORD-456"));
        }
    }

    @Test
    public void shouldCallAllMappersInCorrectOrder() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createScheduledOrderDetails();

        hl7MessageCreator.createHL7Message(orderDetails);

        verify(orcMapper, times(1)).mapScheduledOrder(any(ORC.class), eq(orderDetails));
        verify(zdsMapper, times(1)).mapStudyInstanceUID(any(ORM_O01.class), eq("ORD-123"), eq(orderDetails.getDateCreated()));
        verify(messageHeaderMapper, times(1)).map(any(MSH.class), eq(orderDetails));
        verify(patientIdentificationMapper, times(1)).map(any(PID.class), eq(orderDetails));
        verify(obrMapper, times(1)).map(any(OBR.class), eq(orderDetails));
    }

    @Test
    public void shouldUseCorrectStudyInstanceUIDParametersForScheduledOrder() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createScheduledOrderDetails();
        Date orderDate = orderDetails.getDateCreated();

        hl7MessageCreator.createHL7Message(orderDetails);

        ArgumentCaptor<String> orderNumberCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);

        verify(zdsMapper).mapStudyInstanceUID(
                any(ORM_O01.class),
                orderNumberCaptor.capture(),
                dateCaptor.capture()
        );

        assertEquals("ORD-123", orderNumberCaptor.getValue());
        assertEquals(orderDate, dateCaptor.getValue());
    }

    @Test
    public void shouldUseCorrectStudyInstanceUIDParametersForDiscontinuedOrder() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createDiscontinuedOrderDetails();
        Order previousOrder = new Order();
        previousOrder.setOrderUuid("previous-uuid");
        Date previousOrderDate = orderDetails.getPreviousOrder().getDateCreated();

        when(orderRepository.findByOrderUuid("previous-uuid")).thenReturn(previousOrder);

        hl7MessageCreator.createHL7Message(orderDetails);

        ArgumentCaptor<String> orderNumberCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);

        verify(zdsMapper).mapStudyInstanceUID(
                any(ORM_O01.class),
                orderNumberCaptor.capture(),
                dateCaptor.capture()
        );

        assertEquals("PREV-ORD-123", orderNumberCaptor.getValue());
        assertEquals(previousOrderDate, dateCaptor.getValue());
    }

    private OpenMRSOrderDetails createScheduledOrderDetails() {
        OpenMRSOrderDetails orderDetails = new OpenMRSOrderDetails();
        orderDetails.setUuid("order-uuid");
        orderDetails.setOrderNumber("ORD-123");
        orderDetails.setAction("NEW");
        orderDetails.setDateCreated(new Date());
        return orderDetails;
    }

    private OpenMRSOrderDetails createDiscontinuedOrderDetails() {
        OpenMRSOrderDetails orderDetails = new OpenMRSOrderDetails();
        orderDetails.setUuid("order-uuid");
        orderDetails.setOrderNumber("ORD-456");
        orderDetails.setAction("DISCONTINUE");
        orderDetails.setDateCreated(new Date());

        BaseOrderDetails previousOrder = new BaseOrderDetails();
        previousOrder.setUuid("previous-uuid");
        previousOrder.setOrderNumber("PREV-ORD-123");
        previousOrder.setDateCreated(new Date());

        orderDetails.setPreviousOrder(previousOrder);

        return orderDetails;
    }
}
