package org.bahmni.module.pacsintegration.repository;

import org.bahmni.module.pacsintegration.model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Integer> {
}
