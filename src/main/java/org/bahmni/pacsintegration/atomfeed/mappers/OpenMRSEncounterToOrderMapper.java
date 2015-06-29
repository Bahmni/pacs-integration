package org.bahmni.pacsintegration.atomfeed.mappers;

import org.bahmni.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.pacsintegration.model.Order;
import org.bahmni.pacsintegration.model.OrderType;
import org.bahmni.pacsintegration.repository.OrderRepository;
import org.bahmni.pacsintegration.repository.OrderTypeRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class OpenMRSEncounterToOrderMapper {
    public Collection<Order> map(OpenMRSEncounter openMRSEncounter, List<OrderType> acceptableOrderTypes, OrderRepository orderRepository) {
        Collection<Order> orders = new ArrayList<Order>();
        for (OpenMRSOrder openMRSOrder : openMRSEncounter.getTestOrders()) {
            OrderType orderType = findOrderType(acceptableOrderTypes, openMRSOrder.getOrderType());
            Order existingOrder = orderRepository.getByOrderUuid(openMRSOrder.getUuid());
            if (orderType != null && existingOrder == null && !openMRSOrder.isVoided()) {
                Order order = new Order();
                order.setOrderUuid(openMRSOrder.getUuid());
                order.setTestName(openMRSOrder.getConcept().getName().getName());
                order.setTestUuid(openMRSOrder.getConcept().getUuid());
                order.setOrderType(orderType);
                orders.add(order);
            }
        }
        return orders;
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
