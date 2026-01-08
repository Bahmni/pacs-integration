package org.bahmni.module.pacsintegration.services;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.v25.message.ADR_A19;
import org.bahmni.module.pacsintegration.atomfeed.builders.OpenMRSConceptBuilder;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.LocationDTO;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderLocationInfo;
import org.bahmni.module.pacsintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.pacsintegration.atomfeed.mappers.OpenMRSEncounterToOrderMapper;
import org.bahmni.module.pacsintegration.exception.HL7MessageException;
import org.bahmni.module.pacsintegration.model.ImagingStudyReference;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.model.OrderDetails;
import org.bahmni.module.pacsintegration.model.OrderType;
import org.bahmni.module.pacsintegration.repository.ImagingStudyReferenceRepository;
import org.bahmni.module.pacsintegration.repository.OrderDetailsRepository;
import org.bahmni.module.pacsintegration.repository.OrderRepository;
import org.bahmni.module.pacsintegration.repository.OrderTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PacsIntegrationServiceTest {
    @Mock
    OrderTypeRepository orderTypeRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderDetailsRepository orderDetailsRepository;

    @Mock
    OpenMRSEncounterToOrderMapper openMRSEncounterToOrderMapper;

    @InjectMocks
    PacsIntegrationService pacsIntegrationService;

    @Mock
    private OpenMRSService openMRSService;

    @Mock
    private HL7MessageCreator hl7MessageCreator;

    @Mock
    ADR_A19 adr_a19;

    @Mock
    private ModalityService modalityService;

    @Mock
    private ImagingStudyService imagingStudyService;

    @Mock
    private StudyInstanceUIDGenerator studyInstanceUIDGenerator;

    @Mock
    private LocationResolver locationResolver;

    @Mock
    private ImagingStudyReferenceRepository imagingStudyReferenceRepository;

    private String PATIENT_UUID = "patient1";


    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(pacsIntegrationService, "imagingStudyEnabled", true);
    }

    @Test
    public void shouldProcessAnEncounterWithTwoOrders() throws LLPException, HL7Exception, ParseException, IOException {
        OpenMRSEncounter encounter = buildEncounter();
        when(openMRSService.getPatient(PATIENT_UUID)).thenReturn(new OpenMRSPatient());
        when(orderTypeRepository.findAll()).thenReturn(getAcceptableOrderTypes());
        when(orderRepository.findByOrderUuid(any(String.class))).thenReturn(null);
        when(hl7MessageCreator.createHL7Message(any())).thenReturn(adr_a19);
        when(adr_a19.encode()).thenReturn("Request message");
        when(modalityService.sendMessage(any(AbstractMessage.class), any(String.class))).thenReturn("Response message");
        when(openMRSEncounterToOrderMapper.map(any(OpenMRSOrder.class), any(OpenMRSEncounter.class), any(List.class)))
                .thenReturn(new Order());
        when(studyInstanceUIDGenerator.generateStudyInstanceUID(any(String.class), any(Date.class))).thenReturn("test-study-instance-uid");
        when(openMRSService.getOrderDetails(anyString())).thenReturn(new OpenMRSOrderDetails());
        when(locationResolver.resolveLocations(any(OpenMRSOrderDetails.class))).thenReturn(buildOrderLocationInfo());
        when(imagingStudyReferenceRepository.findByOrderId(null)).thenReturn(Collections.emptyList());

        pacsIntegrationService.processEncounter(encounter);

        verify(orderRepository, times(2)).save(any(Order.class));
        verify(orderDetailsRepository, times(2)).save(any(OrderDetails.class));
        verify(imagingStudyService, times(2)).createImagingStudy(anyString(), eq(PATIENT_UUID), anyString(), anyString(), anyString());
    }

    @Test
    public void shouldNotProcessAlreadyProcessedOrder() throws IOException, ParseException, LLPException, HL7Exception {
        OpenMRSEncounter encounter = buildEncounter();
        Order existingOrder = new Order();
        existingOrder.setId(2);
        
        when(openMRSService.getPatient(PATIENT_UUID)).thenReturn(new OpenMRSPatient());
        when(orderTypeRepository.findAll()).thenReturn(getAcceptableOrderTypes());
        when(orderRepository.findByOrderUuid(any(String.class))).thenReturn(null).thenReturn(existingOrder);
        when(hl7MessageCreator.createHL7Message(any())).thenReturn(adr_a19);
        when(adr_a19.encode()).thenReturn("Request message");
        when(modalityService.sendMessage(any(AbstractMessage.class), any(String.class))).thenReturn("Response message");
        when(openMRSEncounterToOrderMapper.map(any(OpenMRSOrder.class), any(OpenMRSEncounter.class), any(List.class)))
                .thenReturn(new Order());
        when(studyInstanceUIDGenerator.generateStudyInstanceUID(any(String.class), any(Date.class))).thenReturn("test-study-instance-uid");
        when(openMRSService.getOrderDetails(anyString())).thenReturn(new OpenMRSOrderDetails());
        when(locationResolver.resolveLocations(any(OpenMRSOrderDetails.class))).thenReturn(buildOrderLocationInfo());
        
        when(imagingStudyReferenceRepository.findByOrderId(null)).thenReturn(Collections.emptyList());
        when(imagingStudyReferenceRepository.findByOrderId(2)).thenReturn(Collections.singletonList(new ImagingStudyReference()));

        pacsIntegrationService.processEncounter(encounter);

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderDetailsRepository, times(1)).save(any(OrderDetails.class));
        verify(imagingStudyService, times(1)).createImagingStudy(anyString(), eq(PATIENT_UUID), anyString(), anyString(), anyString());
    }

    @Test
    public void shouldNotCreateImagingStudyWhenDisabled() throws IOException, ParseException, LLPException, HL7Exception {
        ReflectionTestUtils.setField(pacsIntegrationService, "imagingStudyEnabled", false);
        
        OpenMRSEncounter encounter = buildEncounter();
        when(openMRSService.getPatient(PATIENT_UUID)).thenReturn(new OpenMRSPatient());
        when(orderTypeRepository.findAll()).thenReturn(getAcceptableOrderTypes());
        when(orderRepository.findByOrderUuid(any(String.class))).thenReturn(null);
        when(openMRSService.getOrderDetails(anyString())).thenReturn(new OpenMRSOrderDetails());
        when(hl7MessageCreator.createHL7Message(any())).thenReturn(adr_a19);
        when(adr_a19.encode()).thenReturn("Request message");
        when(modalityService.sendMessage(any(AbstractMessage.class), any(String.class))).thenReturn("Response message");
        when(openMRSEncounterToOrderMapper.map(any(OpenMRSOrder.class), any(OpenMRSEncounter.class), any(List.class)))
                .thenReturn(new Order());

        pacsIntegrationService.processEncounter(encounter);

        verify(orderRepository, times(2)).save(any(Order.class));
        verify(orderDetailsRepository, times(2)).save(any(OrderDetails.class));
        verify(imagingStudyService, never()).createImagingStudy(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(openMRSService, times(2)).getOrderDetails(anyString());
        verify(locationResolver, never()).resolveLocations(any(OpenMRSOrderDetails.class));
        verify(studyInstanceUIDGenerator, never()).generateStudyInstanceUID(any(String.class), any(Date.class));
    }

    @Test
    public void shouldCreateImagingStudyWithCorrectParameters() throws IOException, ParseException, LLPException, HL7Exception {
        OpenMRSEncounter encounter = buildEncounter();
        String expectedStudyInstanceUID = "1.2.826.0.1.3680043.8.498.12345";
        String expectedLocationUuid = "location-uuid-123";

        LocationDTO sourceLocation = new LocationDTO();
        sourceLocation.setUuid(expectedLocationUuid);
        
        OrderLocationInfo locationInfo = new OrderLocationInfo();
        locationInfo.setSourceLocation(sourceLocation);
        
        when(openMRSService.getPatient(PATIENT_UUID)).thenReturn(new OpenMRSPatient());
        when(orderTypeRepository.findAll()).thenReturn(getAcceptableOrderTypes());
        when(orderRepository.findByOrderUuid(any(String.class))).thenReturn(null);
        when(openMRSService.getOrderDetails("uuid1")).thenReturn(new OpenMRSOrderDetails());
        when(hl7MessageCreator.createHL7Message(any())).thenReturn(adr_a19);
        when(adr_a19.encode()).thenReturn("Request message");
        when(modalityService.sendMessage(any(AbstractMessage.class), any(String.class))).thenReturn("Response message");
        when(openMRSEncounterToOrderMapper.map(any(OpenMRSOrder.class), any(OpenMRSEncounter.class), any(List.class)))
                .thenReturn(new Order());
        when(studyInstanceUIDGenerator.generateStudyInstanceUID(eq("ORD-1"), any(Date.class))).thenReturn(expectedStudyInstanceUID);
        when(locationResolver.resolveLocations(any(OpenMRSOrderDetails.class))).thenReturn(locationInfo);

        pacsIntegrationService.processEncounter(encounter);

        verify(imagingStudyService).createImagingStudy(
                eq("uuid1"),
                eq(PATIENT_UUID),
                eq(expectedLocationUuid),
                eq(expectedStudyInstanceUID),
                eq("Imaging Study for Test Order 1")
        );
    }

    @Test(expected = IOException.class)
    public void shouldPropagateExceptionWhenImagingStudyCreationFails() throws IOException, ParseException, LLPException, HL7Exception {
        OpenMRSEncounter encounter = buildEncounter();
        
        when(openMRSService.getPatient(PATIENT_UUID)).thenReturn(new OpenMRSPatient());
        when(orderTypeRepository.findAll()).thenReturn(getAcceptableOrderTypes());
        when(orderRepository.findByOrderUuid(any(String.class))).thenReturn(null);
        when(hl7MessageCreator.createHL7Message(any())).thenReturn(adr_a19);
        when(adr_a19.encode()).thenReturn("Request message");
        when(modalityService.sendMessage(any(AbstractMessage.class), any(String.class))).thenReturn("Response message");
        when(openMRSEncounterToOrderMapper.map(any(OpenMRSOrder.class), any(OpenMRSEncounter.class), any(List.class)))
                .thenReturn(new Order());
        when(studyInstanceUIDGenerator.generateStudyInstanceUID(any(String.class), any(Date.class))).thenReturn("test-study-instance-uid");
        when(openMRSService.getOrderDetails(anyString())).thenReturn(new OpenMRSOrderDetails());
        when(locationResolver.resolveLocations(any(OpenMRSOrderDetails.class))).thenReturn(buildOrderLocationInfo());
        when(imagingStudyReferenceRepository.findByOrderId(null)).thenReturn(Collections.emptyList());
        when(imagingStudyService.createImagingStudy(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new IOException("ImagingStudy creation failed"));

        pacsIntegrationService.processEncounter(encounter);
    }

    @Test
    public void shouldRetryOnlyImagingStudyCreationWhenOrderAlreadyExists() throws IOException, ParseException, LLPException, HL7Exception {
        OpenMRSEncounter encounter = buildEncounter();
        Order existingOrder = new Order();
        existingOrder.setId(1);
        
        when(openMRSService.getPatient(PATIENT_UUID)).thenReturn(new OpenMRSPatient());
        when(orderTypeRepository.findAll()).thenReturn(getAcceptableOrderTypes());
        when(orderRepository.findByOrderUuid("uuid1")).thenReturn(existingOrder);
        when(orderRepository.findByOrderUuid("uuid2")).thenReturn(null);
        when(hl7MessageCreator.createHL7Message(any())).thenReturn(adr_a19);
        when(adr_a19.encode()).thenReturn("Request message");
        when(modalityService.sendMessage(any(AbstractMessage.class), any(String.class))).thenReturn("Response message");
        when(openMRSEncounterToOrderMapper.map(any(OpenMRSOrder.class), any(OpenMRSEncounter.class), any(List.class)))
                .thenReturn(new Order());
        when(studyInstanceUIDGenerator.generateStudyInstanceUID(any(String.class), any(Date.class))).thenReturn("test-study-instance-uid");
        when(openMRSService.getOrderDetails(anyString())).thenReturn(new OpenMRSOrderDetails());
        when(locationResolver.resolveLocations(any(OpenMRSOrderDetails.class))).thenReturn(buildOrderLocationInfo());
        when(imagingStudyReferenceRepository.findByOrderId(1)).thenReturn(Collections.emptyList());
        when(imagingStudyReferenceRepository.findByOrderId(null)).thenReturn(Collections.emptyList());

        pacsIntegrationService.processEncounter(encounter);

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderDetailsRepository, times(1)).save(any(OrderDetails.class));
        verify(imagingStudyService, times(2)).createImagingStudy(anyString(), eq(PATIENT_UUID), anyString(), anyString(), anyString());
    }

    @Test(expected = HL7MessageException.class)
    public void shouldPropagateExceptionWhenHL7MessageCreationFails() throws IOException, ParseException, LLPException, HL7Exception {
        OpenMRSEncounter encounter = buildEncounter();
        
        when(openMRSService.getPatient(PATIENT_UUID)).thenReturn(new OpenMRSPatient());
        when(orderTypeRepository.findAll()).thenReturn(getAcceptableOrderTypes());
        when(orderRepository.findByOrderUuid(any(String.class))).thenReturn(null);
        when(openMRSService.getOrderDetails(anyString())).thenReturn(new OpenMRSOrderDetails());
        when(hl7MessageCreator.createHL7Message(any())).thenThrow(new HL7MessageException("Unable to Cancel the Order. Previous order is not found/processed"));

        pacsIntegrationService.processEncounter(encounter);
    }

    private OrderLocationInfo buildOrderLocationInfo() {
        LocationDTO location = new LocationDTO();
        location.setUuid("location-uuid");
        location.setName("Test Location");
        
        OrderLocationInfo locationInfo = new OrderLocationInfo();
        locationInfo.setSourceLocation(location);
        locationInfo.setFulfillingLocation(location);
        
        return locationInfo;
    }

    OpenMRSEncounter buildEncounter() {
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setPatientUuid(PATIENT_UUID);

        OpenMRSConcept concept1 = new OpenMRSConceptBuilder()
                .addConceptName("Test Order 1")
                .build();
        OpenMRSOrder order1 = new OpenMRSOrder("uuid1", "type1", concept1, false, null, null);
        order1.setOrderNumber("ORD-1");
        order1.setDateCreated(new Date());

        OpenMRSConcept concept2 = new OpenMRSConceptBuilder()
                .addConceptName("Test Order 2")
                .build();
        OpenMRSOrder order2 = new OpenMRSOrder("uuid2", "type2", concept2, false, null, null);
        order2.setOrderNumber("ORD-2");
        order2.setDateCreated(new Date());
        
        openMRSEncounter.setOrders(Arrays.asList(order1, order2));
        return openMRSEncounter;
    }

    List<OrderType> getAcceptableOrderTypes() {
        ArrayList<OrderType> acceptableOrderTypes = new ArrayList<OrderType>();
        acceptableOrderTypes.add(new OrderType(1, "type1", null));
        acceptableOrderTypes.add(new OrderType(2, "type2", null));
        return acceptableOrderTypes;
    }

}
