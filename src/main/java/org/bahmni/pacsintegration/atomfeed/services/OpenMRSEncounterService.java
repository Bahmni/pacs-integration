package org.bahmni.pacsintegration.atomfeed.services;

import org.bahmni.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.pacsintegration.atomfeed.mappers.OpenMRSEncounterToOrderMapper;
import org.bahmni.pacsintegration.model.Order;
import org.bahmni.pacsintegration.model.OrderType;
import org.bahmni.pacsintegration.repository.OrderRepository;
import org.bahmni.pacsintegration.repository.OrderTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class OpenMRSEncounterService {
    @Autowired
    private OpenMRSEncounterToOrderMapper openMRSEncounterToOrderMapper;

    @Autowired
    private OrderTypeRepository orderTypeRepository;

    @Autowired
    private OrderRepository orderRepository;

    public void save(OpenMRSEncounter openMRSEncounter) {
        List<OrderType> acceptableOrderTypes = orderTypeRepository.findAll();

        Collection<Order> mappedOrders = openMRSEncounterToOrderMapper.map(openMRSEncounter, acceptableOrderTypes, orderRepository);

        orderRepository.save(mappedOrders);
    }
}
