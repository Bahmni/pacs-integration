package org.bahmni.pacsintegration.repository;


import org.bahmni.pacsintegration.model.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderTypeRepository extends JpaRepository<OrderType, Integer> {
    OrderType getByName(String name);
}
