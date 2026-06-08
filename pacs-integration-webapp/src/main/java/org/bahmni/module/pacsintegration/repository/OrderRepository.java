package org.bahmni.module.pacsintegration.repository;

import org.bahmni.module.pacsintegration.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Order findByOrderUuid(String orderUuid);
}
