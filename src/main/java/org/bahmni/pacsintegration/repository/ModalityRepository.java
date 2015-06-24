package org.bahmni.pacsintegration.repository;

import org.bahmni.pacsintegration.model.Modality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModalityRepository extends JpaRepository<Modality, Integer> {
}
