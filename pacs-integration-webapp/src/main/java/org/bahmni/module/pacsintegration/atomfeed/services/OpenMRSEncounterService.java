package org.bahmni.module.pacsintegration.atomfeed.services;

import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.pacsintegration.atomfeed.mappers.OpenMRSEncounterToOrderMapper;
import org.bahmni.module.pacsintegration.model.Orders;
import org.bahmni.module.pacsintegration.model.OrderType;
import org.bahmni.module.pacsintegration.repository.OrderRepository;
import org.bahmni.module.pacsintegration.repository.OrderTypeRepository;
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

        Collection<Orders> mappedOrders = openMRSEncounterToOrderMapper.map(openMRSEncounter, acceptableOrderTypes, orderRepository);

        orderRepository.save(mappedOrders);
    }
}
