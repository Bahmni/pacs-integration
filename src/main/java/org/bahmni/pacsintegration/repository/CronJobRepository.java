package org.bahmni.pacsintegration.repository;

import org.bahmni.pacsintegration.model.QuartzCronScheduler;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CronJobRepository extends JpaRepository<QuartzCronScheduler, Integer> {
}
