package org.bahmni.module.pacsintegration.repository;

import org.bahmni.module.pacsintegration.model.Modality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModalityRepository extends JpaRepository<Modality, Integer> {
    List<Modality> findByName(String name);
}
