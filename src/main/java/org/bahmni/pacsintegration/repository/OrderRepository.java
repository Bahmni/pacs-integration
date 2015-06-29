package org.bahmni.pacsintegration.repository;

import org.bahmni.pacsintegration.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Order getByOrderUuid(String uuid);
}
