package org.bahmni.module.pacsintegration.repository;

import org.bahmni.module.pacsintegration.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface OrderRepository extends JpaRepository<Orders, Integer> {
    Orders findByOrderUuid(String orderUuid);
}
