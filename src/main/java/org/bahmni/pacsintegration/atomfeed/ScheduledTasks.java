package org.bahmni.pacsintegration.atomfeed;

import org.apache.log4j.Logger;
import org.bahmni.pacsintegration.atomfeed.jobs.Job;
import org.bahmni.pacsintegration.model.CronJob;
import org.bahmni.pacsintegration.repository.CronJobRepository;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class ScheduledTasks implements SchedulingConfigurer {

    @Autowired
    private Environment env;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    private CronJobRepository cronJobRepository;

    private static Logger logger = Logger.getLogger(ScheduledTasks.class);

    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(100);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        List<CronJob> crobJobs = cronJobRepository.findAll();
        taskRegistrar.setScheduler(taskExecutor());
        for (final CronJob cronJob : crobJobs) {
            CronExpression cronExpression = null;
            try {
                cronExpression = new CronExpression(cronJob.getCronStatement());
            } catch (ParseException e) {
                logger.error("Could not parse the cron statement: " + cronJob.getCronStatement() + " for: " + cronJob.getName());
                continue;
            }

            final CronExpression finalCronExpression = cronExpression;

            taskRegistrar.addTriggerTask(
                    new Runnable() {
                        @Override
                        public void run() {
                            Job applicationContextBean = (Job) applicationContext.getBean(cronJob.getName());
                            applicationContextBean.process();
                        }
                    }, new Trigger() {
                        @Override
                        public Date nextExecutionTime(TriggerContext triggerContext) {
                            Calendar nextExecutionTime = new GregorianCalendar();
                            Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
                            nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());

                            Date now = new Date();
                            long nextExecutionTimeByStatement = finalCronExpression.getNextValidTimeAfter(now).getTime();

                            nextExecutionTime.add(Calendar.MILLISECOND, (int) (nextExecutionTimeByStatement - now.getTime()));
                            return nextExecutionTime.getTime();
                        }
                    }
            );
        }
    }
}