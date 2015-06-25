package org.bahmni.pacsintegration.repository;

import org.bahmni.pacsintegration.model.QuartzScheduler;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuartzSchedulerRepository extends JpaRepository<QuartzScheduler, Integer> {
}
