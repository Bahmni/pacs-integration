package org.bahmni.pacsintegration.atomfeed.mappers;

import org.bahmni.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.pacsintegration.model.Orders;
import org.bahmni.pacsintegration.model.OrderType;
import org.bahmni.pacsintegration.repository.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class OpenMRSEncounterToOrderMapper {
    public Collection<Orders> map(OpenMRSEncounter openMRSEncounter, List<OrderType> acceptableOrderTypes, OrderRepository orderRepository) {
        Collection<Orders> orderses = new ArrayList<Orders>();
        for (OpenMRSOrder openMRSOrder : openMRSEncounter.getTestOrders()) {
            OrderType orderType = findOrderType(acceptableOrderTypes, openMRSOrder.getOrderType());
            Orders existingOrders = orderRepository.findByOrderUuid(openMRSOrder.getUuid());
            if (orderType != null && existingOrders == null && !openMRSOrder.isVoided()) {
                Orders order = new Orders();
                order.setOrderUuid(openMRSOrder.getUuid());
                order.setTestName(openMRSOrder.getConcept().getName().getName());
                order.setTestUuid(openMRSOrder.getConcept().getUuid());
                order.setOrderType(orderType);
                orderses.add(order);
            }
        }
        return orderses;
    }

    private OrderType findOrderType(List<OrderType> acceptableOrderTypes, String orderType) {
        for (OrderType acceptableOrderType : acceptableOrderTypes) {
            if (acceptableOrderType.getName().equals(orderType)) {
                return acceptableOrderType;
            }
        }
        return null;
    }
}
