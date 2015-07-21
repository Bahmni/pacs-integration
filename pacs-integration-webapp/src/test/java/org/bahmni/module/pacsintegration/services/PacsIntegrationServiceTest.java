package org.bahmni.module.pacsintegration.services;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.llp.LLPException;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.pacsintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.pacsintegration.atomfeed.mappers.OpenMRSEncounterToOrderMapper;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.model.OrderType;
import org.bahmni.module.pacsintegration.repository.OrderRepository;
import org.bahmni.module.pacsintegration.repository.OrderTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class PacsIntegrationServiceTest {
    @Mock
    OrderTypeRepository orderTypeRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    OpenMRSEncounterToOrderMapper openMRSEncounterToOrderMapper;

    @InjectMocks
    PacsIntegrationService pacsIntegrationService = new PacsIntegrationService();

    @Mock
    private OpenMRSService openMRSService;

    @Mock
    private HL7Service hl7Service;

    @Mock
    private ModalityService modalityService;

    private String PATIENT_UUID = "patient1";


    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldProcessAnEncounterWithTwoOrders() throws LLPException, HL7Exception, ParseException, IOException {
        OpenMRSEncounter encounter = buildEncounter();
        when(openMRSService.getPatient(PATIENT_UUID)).thenReturn(new OpenMRSPatient());
        when(orderTypeRepository.findAll()).thenReturn(getAcceptableOrderTypes());
        when(orderRepository.findByOrderUuid(any(String.class))).thenReturn(null);

        pacsIntegrationService.processEncounter(encounter);

        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    public void shouldNotProcessAlreadyProcessedOrder() throws IOException, ParseException, LLPException, HL7Exception {
        OpenMRSEncounter encounter = buildEncounter();
        when(openMRSService.getPatient(PATIENT_UUID)).thenReturn(new OpenMRSPatient());
        when(orderTypeRepository.findAll()).thenReturn(getAcceptableOrderTypes());
        when(orderRepository.findByOrderUuid(any(String.class))).thenReturn(null).thenReturn(new Order());

        pacsIntegrationService.processEncounter(encounter);

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    OpenMRSEncounter buildEncounter() {
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setPatientUuid(PATIENT_UUID);
        OpenMRSOrder order1 = new OpenMRSOrder("uuid1", "type1", null, false, null, null);
        OpenMRSOrder order2 = new OpenMRSOrder("uuid2", "type2", null, false, null, null);
        openMRSEncounter.setTestOrders(Arrays.asList(order1, order2));
        return openMRSEncounter;
    }

    List<OrderType> getAcceptableOrderTypes() {
        ArrayList<OrderType> acceptableOrderTypes = new ArrayList<OrderType>();
        acceptableOrderTypes.add(new OrderType(1, "type1", null));
        acceptableOrderTypes.add(new OrderType(2, "type2", null));
        return acceptableOrderTypes;
    }


}