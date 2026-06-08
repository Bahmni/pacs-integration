package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.LocationDTO;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderLocationInfo;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.Constants;
import org.bahmni.module.pacsintegration.integrationtest.HL7Utils;
import org.bahmni.module.pacsintegration.services.LocationResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MessageHeaderMapperImplTest {

    private static final String SENDING_APPLICATION = "Test EMR";
    private static final String SENDING_FACILITY = "Test Hospital";
    private static final String RECEIVING_APPLICATION = "Test PACS";
    private static final String RECEIVING_FACILITY = "Test Radiology";

    @Mock
    private LocationResolver locationResolver;

    private MessageHeaderMapperImpl messageHeaderMapper;
    private MSH messageHeader;
    private OpenMRSOrderDetails orderDetails;

    @Before
    public void setUp() throws HL7Exception {
        messageHeaderMapper = new MessageHeaderMapperImpl(
                SENDING_APPLICATION,
                SENDING_FACILITY,
                RECEIVING_APPLICATION,
                RECEIVING_FACILITY,
                locationResolver
        );

        ORM_O01 message = new ORM_O01();
        messageHeader = message.getMSH();
        orderDetails = HL7Utils.createScheduledOrderDetails();
    }

    @Test
    public void shouldMapMessageHeaderWithAllRequiredFields() throws HL7Exception {
        OrderLocationInfo locationInfo = createOrderLocationInfo();

        when(locationResolver.resolveLocations(orderDetails)).thenReturn(locationInfo);

        messageHeaderMapper.map(messageHeader, orderDetails);

        assertEquals(SENDING_APPLICATION, messageHeader.getSendingApplication().getNamespaceID().getValue());
        assertEquals("Source Location", messageHeader.getSendingFacility().getNamespaceID().getValue());
        assertEquals(RECEIVING_APPLICATION, messageHeader.getReceivingApplication().getNamespaceID().getValue());
        assertEquals("Fulfilling Location", messageHeader.getReceivingFacility().getNamespaceID().getValue());
        assertNotNull(messageHeader.getMessageControlID().getValue());
    }

    @Test
    public void shouldSetHL7Constants() throws HL7Exception {
        OrderLocationInfo locationInfo = createOrderLocationInfo();

        when(locationResolver.resolveLocations(orderDetails)).thenReturn(locationInfo);

        messageHeaderMapper.map(messageHeader, orderDetails);

        assertEquals(Constants.FIELD_SEPARATOR, messageHeader.getFieldSeparator().getValue());
        assertEquals(Constants.ENCODING_CHARACTERS, messageHeader.getEncodingCharacters().getValue());
        assertEquals(Constants.HL7_PROCESSING_PROD_MODE, messageHeader.getProcessingID().getProcessingID().getValue());
        assertEquals(Constants.HL7_MESSAGE_VERSION, messageHeader.getVersionID().getVersionID().getValue());
        assertEquals(Constants.HL7_MESSAGE_CODE, messageHeader.getMessageType().getMessageCode().getValue());
        assertEquals(Constants.HL7_TRIGGER_EVENT, messageHeader.getMessageType().getTriggerEvent().getValue());
    }

    @Test
    public void shouldSetDateTimeOfMessage() throws HL7Exception {
        OrderLocationInfo locationInfo = createOrderLocationInfo();

        when(locationResolver.resolveLocations(orderDetails)).thenReturn(locationInfo);

        messageHeaderMapper.map(messageHeader, orderDetails);

        assertNotNull(messageHeader.getDateTimeOfMessage().getTime().getValue());
        assertEquals(14, messageHeader.getDateTimeOfMessage().getTime().getValue().length());
    }

    @Test
    public void shouldCallLocationResolverWithOrderDetails() throws HL7Exception {
        OrderLocationInfo locationInfo = createOrderLocationInfo();

        when(locationResolver.resolveLocations(orderDetails)).thenReturn(locationInfo);

        messageHeaderMapper.map(messageHeader, orderDetails);

        verify(locationResolver, times(1)).resolveLocations(orderDetails);
    }

    @Test
    public void shouldMapSourceLocationToSendingFacility() throws HL7Exception {
        OrderLocationInfo locationInfo = createOrderLocationInfo();
        locationInfo.getSourceLocation().setName("Custom Source Location");

        when(locationResolver.resolveLocations(orderDetails)).thenReturn(locationInfo);

        messageHeaderMapper.map(messageHeader, orderDetails);

        assertEquals("Custom Source Location", messageHeader.getSendingFacility().getNamespaceID().getValue());
    }

    @Test
    public void shouldMapFulfillingLocationToReceivingFacility() throws HL7Exception {
        OrderLocationInfo locationInfo = createOrderLocationInfo();
        locationInfo.getFulfillingLocation().setName("Custom Fulfilling Location");

        when(locationResolver.resolveLocations(orderDetails)).thenReturn(locationInfo);

        messageHeaderMapper.map(messageHeader, orderDetails);

        assertEquals("Custom Fulfilling Location", messageHeader.getReceivingFacility().getNamespaceID().getValue());
    }

    @Test
    public void shouldGenerateMessageControlIDWithOrderNumber() throws HL7Exception {
        orderDetails.setOrderNumber("ORD-12345");
        OrderLocationInfo locationInfo = createOrderLocationInfo();

        when(locationResolver.resolveLocations(orderDetails)).thenReturn(locationInfo);

        messageHeaderMapper.map(messageHeader, orderDetails);

        String messageControlID = messageHeader.getMessageControlID().getValue();
        assertNotNull(messageControlID);
        assertTrue(messageControlID.contains("12345"));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionWhenHL7ExceptionOccurs() throws HL7Exception {

        when(locationResolver.resolveLocations(orderDetails)).thenThrow(new RuntimeException("Test exception"));

        messageHeaderMapper.map(messageHeader, orderDetails);
    }

    @Test
    public void shouldGenerateMessageControlIDForShortOrderNumber() {
        String result = messageHeaderMapper.generateMessageControlID("ORD-1");

        assertNotNull(result);
        assertTrue(result.length() > 0);
    }

    @Test
    public void shouldGenerateMessageControlIDForLongOrderNumber() {
        String result = messageHeaderMapper.generateMessageControlID("ORD-123456789");

        assertNotNull(result);
        assertTrue(result.contains("12345"));
    }

    @Test
    public void shouldUseSendingApplicationFromConstructor() throws HL7Exception {
        OrderLocationInfo locationInfo = createOrderLocationInfo();

        when(locationResolver.resolveLocations(orderDetails)).thenReturn(locationInfo);

        messageHeaderMapper.map(messageHeader, orderDetails);

        assertEquals(SENDING_APPLICATION, messageHeader.getSendingApplication().getNamespaceID().getValue());
    }

    @Test
    public void shouldUseReceivingApplicationFromConstructor() throws HL7Exception {
        OrderLocationInfo locationInfo = createOrderLocationInfo();

        when(locationResolver.resolveLocations(orderDetails)).thenReturn(locationInfo);

        messageHeaderMapper.map(messageHeader, orderDetails);

        assertEquals(RECEIVING_APPLICATION, messageHeader.getReceivingApplication().getNamespaceID().getValue());
    }

    private OrderLocationInfo createOrderLocationInfo() {
        LocationDTO sourceLocation = new LocationDTO();
        sourceLocation.setUuid("source-uuid");
        sourceLocation.setName("Source Location");
        sourceLocation.setDisplay("Source Location Display");

        LocationDTO fulfillingLocation = new LocationDTO();
        fulfillingLocation.setUuid("fulfilling-uuid");
        fulfillingLocation.setName("Fulfilling Location");
        fulfillingLocation.setDisplay("Fulfilling Location Display");

        return new OrderLocationInfo(fulfillingLocation, sourceLocation);
    }
}
