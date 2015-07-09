package org.bahmni.module.pacsintegration.atomfeed.mappers;

import org.bahmni.module.pacsintegration.atomfeed.builders.*;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConceptName;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.model.OrderType;
import org.bahmni.module.pacsintegration.repository.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenMRSEncounterToOrderMapperTest {
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
        OpenMRSOrder radiologyOrder = new OpenMRSOrderBuilder().withOrderUuid("radiology order uuid").withOrderType("Radiology Order").withOrderNumber("ORD-001").withVoided(false).withConcept(concept).build();
        OpenMRSOrder labOrder = new OpenMRSOrderBuilder().withOrderUuid("lab order uuid").withOrderType("Lab Order").withVoided(false).withConcept(concept).build();
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounterBuilder().withEncounterUuid("encounter uuid").withPatientUuid("patient uuid").withTestOrder(radiologyOrder).withTestOrder(labOrder).build();

        ArrayList<OrderType> acceptableOrderTypes = new ArrayList<OrderType>();
        acceptableOrderTypes.add(new OrderTypeBuilder().withName("Radiology Order").build());

        when(orderRepository.findByOrderUuid("radiology order uuid")).thenReturn(null);

        Order order = openMRSEncounterToOrderMapper.map(radiologyOrder, openMRSEncounter, acceptableOrderTypes);

        assertNotNull(order);
        assertEquals("radiology order uuid", order.getOrderUuid());
    }



}