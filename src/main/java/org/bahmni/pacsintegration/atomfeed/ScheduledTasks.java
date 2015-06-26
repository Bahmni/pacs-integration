package org.bahmni.pacsintegration.atomfeed;

import org.apache.log4j.Logger;
import org.bahmni.pacsintegration.atomfeed.jobs.OpenMRSFeedJob;
import org.bahmni.pacsintegration.model.QuartzCronScheduler;
import org.bahmni.pacsintegration.repository.CronJobRepository;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
public class ScheduledTasks implements SchedulingConfigurer {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CronJobRepository cronJobRepository;

    private Map<String, OpenMRSFeedJob> jobs = new HashMap<String, OpenMRSFeedJob>();

    private static Logger logger = Logger.getLogger(ScheduledTasks.class);

    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(1);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        final List<QuartzCronScheduler> crobJobs = cronJobRepository.findAll();

        for (final QuartzCronScheduler quartzCronScheduler : crobJobs) {
            jobs.put(quartzCronScheduler.getName(), (OpenMRSFeedJob) applicationContext.getBean(quartzCronScheduler.getName()));

            try {
                taskRegistrar.setScheduler(taskExecutor());
                taskRegistrar.addTriggerTask(getTask(quartzCronScheduler), getTrigger(quartzCronScheduler));
            } catch (ParseException e) {
                logger.error("Could not parse the cron statement: " + quartzCronScheduler.getCronStatement() + " for: " + quartzCronScheduler.getName());
                e.printStackTrace();
            }
        }
    }

    private Trigger getTrigger(QuartzCronScheduler quartzCronScheduler) throws ParseException {
        PeriodicTrigger periodicTrigger;
        Date now = new Date();
        long nextExecutionTimeByStatement = new CronExpression(quartzCronScheduler.getCronStatement()).getNextValidTimeAfter(now).getTime();
        periodicTrigger = new PeriodicTrigger((int) (nextExecutionTimeByStatement - now.getTime()), TimeUnit.MILLISECONDS);
        periodicTrigger.setInitialDelay(quartzCronScheduler.getStartDelay());
        return periodicTrigger;
    }

    private Runnable getTask(final QuartzCronScheduler quartzCronScheduler) {
        return new Runnable() {
            @Override
            public void run() {
                OpenMRSFeedJob openMRSFeedJob = jobs.get(quartzCronScheduler.getName());
                try {
                    openMRSFeedJob.process();
                } catch (InterruptedException e) {
                    logger.warn("Thread interrupted for the job: " + quartzCronScheduler.getName());
                }
            }
        };
    }
}