package org.bahmni.module.pacsintegration.repository;

import org.bahmni.module.pacsintegration.model.QuartzCronScheduler;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CronJobRepository extends JpaRepository<QuartzCronScheduler, Integer> {
}
