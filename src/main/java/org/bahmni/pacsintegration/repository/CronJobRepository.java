package org.bahmni.pacsintegration.repository;

import org.bahmni.pacsintegration.model.CronJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CronJobRepository extends JpaRepository<CronJob, Integer> {
}
