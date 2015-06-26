package org.bahmni.pacsintegration.repository;

import org.bahmni.pacsintegration.model.Modality;
import org.bahmni.pacsintegration.model.OrderType;
import org.jboss.logging.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModalityRepository extends JpaRepository<Modality, Integer> {
    List<Modality> findByName(String name);
}
