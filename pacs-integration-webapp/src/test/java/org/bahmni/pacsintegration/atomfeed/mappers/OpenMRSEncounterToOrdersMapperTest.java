package org.bahmni.pacsintegration.atomfeed.mappers;

import org.bahmni.pacsintegration.atomfeed.builders.OpenMRSConceptBuilder;
import org.bahmni.pacsintegration.atomfeed.builders.OpenMRSConceptNameBuilder;
import org.bahmni.pacsintegration.atomfeed.builders.OpenMRSEncounterBuilder;
import org.bahmni.pacsintegration.atomfeed.builders.OpenMRSOrderBuilder;
import org.bahmni.pacsintegration.atomfeed.builders.OrderTypeBuilder;
import org.bahmni.pacsintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.pacsintegration.atomfeed.contract.encounter.OpenMRSConceptName;
import org.bahmni.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.pacsintegration.model.Orders;
import org.bahmni.pacsintegration.model.OrderType;
import org.bahmni.pacsintegration.repository.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenMRSEncounterToOrdersMapperTest {
    @Mock
    private OrderRepository orderRepository;

    OpenMRSEncounterToOrderMapper openMRSEncounterToOrderMapper = new OpenMRSEncounterToOrderMapper();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldMapOnlyRadiologyOpenMRSOrdersToOrders() throws Exception {
        OpenMRSConceptName conceptName = new OpenMRSConceptNameBuilder().withName("Chest View").build();
        OpenMRSConcept concept = new OpenMRSConceptBuilder().withUuid("concept uuid").withName(conceptName).build();
        OpenMRSOrder radiologyOrder = new OpenMRSOrderBuilder().withOrderUuid("radiology order uuid").withOrderType("Radiology Order").withVoided(false).withConcept(concept).build();
        OpenMRSOrder labOrder = new OpenMRSOrderBuilder().withOrderUuid("lab order uuid").withOrderType("Lab Order").withVoided(false).withConcept(concept).build();
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounterBuilder().withEncounterUuid("encounter uuid").withPatientUuid("patient uuid").withTestOrder(radiologyOrder).withTestOrder(labOrder).build();

        ArrayList<OrderType> acceptableOrderTypes = new ArrayList<OrderType>();
        acceptableOrderTypes.add(new OrderTypeBuilder().withName("Radiology Order").build());

        when(orderRepository.findByOrderUuid("radiology order uuid")).thenReturn(null);

        Collection<Orders> orderses = openMRSEncounterToOrderMapper.map(openMRSEncounter, acceptableOrderTypes, orderRepository);

        assertEquals(1, orderses.size());
        assertEquals("radiology order uuid", orderses.iterator().next().getOrderUuid());
    }

    @Test
    public void shouldNotSaveTheOrderWhichIsAlreadySaved() throws Exception {
        OpenMRSConceptName conceptName = new OpenMRSConceptNameBuilder().withName("Chest View").build();
        OpenMRSConcept concept = new OpenMRSConceptBuilder().withUuid("concept uuid").withName(conceptName).build();
        OpenMRSOrder radiologyOrder1 = new OpenMRSOrderBuilder().withOrderUuid("radiology order uuid1").withOrderType("Radiology Order").withVoided(false).withConcept(concept).build();
        OpenMRSOrder radiologyOrder2 = new OpenMRSOrderBuilder().withOrderUuid("radiology order uuid2").withOrderType("Radiology Order").withVoided(false).withConcept(concept).build();
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounterBuilder().withEncounterUuid("encounter uuid").withPatientUuid("patient uuid").withTestOrder(radiologyOrder1).withTestOrder(radiologyOrder2).build();

        ArrayList<OrderType> acceptableOrderTypes = new ArrayList<OrderType>();
        acceptableOrderTypes.add(new OrderTypeBuilder().withName("Radiology Order").build());

        when(orderRepository.findByOrderUuid("radiology order uuid1")).thenReturn(new Orders());
        when(orderRepository.findByOrderUuid("radiology order uuid2")).thenReturn(null);

        Collection<Orders> orderses = openMRSEncounterToOrderMapper.map(openMRSEncounter, acceptableOrderTypes, orderRepository);

        assertEquals(1, orderses.size());
        assertEquals("radiology order uuid2", orderses.iterator().next().getOrderUuid());

    }

    @Test
    public void shouldNotSaveTheOrderIfTheOrderIsVoided() throws Exception {
        OpenMRSConceptName conceptName = new OpenMRSConceptNameBuilder().withName("Chest View").build();
        OpenMRSConcept concept = new OpenMRSConceptBuilder().withUuid("concept uuid").withName(conceptName).build();
        OpenMRSOrder radiologyOrder1 = new OpenMRSOrderBuilder().withOrderUuid("radiology order uuid1").withOrderType("Radiology Order").withVoided(false).withConcept(concept).build();
        OpenMRSOrder radiologyOrder2 = new OpenMRSOrderBuilder().withOrderUuid("radiology order uuid2").withOrderType("Radiology Order").withVoided(false).withConcept(concept).withVoided(true).build();
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounterBuilder().withEncounterUuid("encounter uuid").withPatientUuid("patient uuid").withTestOrder(radiologyOrder1).withTestOrder(radiologyOrder2).build();

        ArrayList<OrderType> acceptableOrderTypes = new ArrayList<OrderType>();
        acceptableOrderTypes.add(new OrderTypeBuilder().withName("Radiology Order").build());

        when(orderRepository.findByOrderUuid("radiology order uuid1")).thenReturn(null);
        when(orderRepository.findByOrderUuid("radiology order uuid2")).thenReturn(null);

        Collection<Orders> orderses = openMRSEncounterToOrderMapper.map(openMRSEncounter, acceptableOrderTypes, orderRepository);

        assertEquals(1, orderses.size());
        assertEquals("radiology order uuid1", orderses.iterator().next().getOrderUuid());
    }
}