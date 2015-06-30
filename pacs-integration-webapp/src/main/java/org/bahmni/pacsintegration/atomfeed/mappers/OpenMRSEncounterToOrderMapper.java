package org.bahmni.pacsintegration.atomfeed.mappers;

import org.bahmni.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.pacsintegration.model.OrderType;
import org.bahmni.pacsintegration.model.Orders;
import org.bahmni.pacsintegration.repository.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class OpenMRSEncounterToOrderMapper {
    public Collection<Orders> map(OpenMRSEncounter openMRSEncounter, List<OrderType> acceptableOrderTypes, OrderRepository orderRepository) {
        Collection<Orders> orders = new ArrayList<Orders>();
        String providerName = getProviderName(openMRSEncounter);
        for (OpenMRSOrder openMRSOrder : openMRSEncounter.getTestOrders()) {
            OrderType orderType = findOrderType(acceptableOrderTypes, openMRSOrder.getOrderType());
            Orders existingOrders = orderRepository.findByOrderUuid(openMRSOrder.getUuid());
            if (orderType != null && existingOrders == null && !openMRSOrder.isVoided()) {
                Orders order = new Orders();
                order.setOrderUuid(openMRSOrder.getUuid());
                order.setTestName(openMRSOrder.getConcept().getName().getName());
                order.setTestUuid(openMRSOrder.getConcept().getUuid());
                order.setOrderType(orderType);
                order.setDateCreated(new Date());
                order.setCreator(providerName);
                orders.add(order);
            }
        }
        return orders;
    }

    private String getProviderName(OpenMRSEncounter openMRSEncounter) {
        return openMRSEncounter.getProviders().size() > 0 ? openMRSEncounter.getProviders().get(0).getName() != null ? openMRSEncounter.getProviders().get(0).getName() : openMRSEncounter.getProviders().get(0).getUuid() : null;
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
