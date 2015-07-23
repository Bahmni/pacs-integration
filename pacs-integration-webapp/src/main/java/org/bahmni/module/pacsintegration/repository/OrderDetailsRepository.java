package org.bahmni.module.pacsintegration.repository;

import org.bahmni.module.pacsintegration.model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Integer> {
}
