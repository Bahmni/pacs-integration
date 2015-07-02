package org.bahmni.module.pacsintegration.atomfeed.jobs;

import org.apache.log4j.Logger;
import org.bahmni.module.pacsintegration.atomfeed.client.AtomFeedClientFactory;
import org.bahmni.module.pacsintegration.atomfeed.worker.EncounterFeedWorker;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@DisallowConcurrentExecution
@Component("openMRSEncounterFailedFeedJob")
@ConditionalOnExpression("'${enable.scheduling}'=='true'")
public class EncounterFailedFeedJob implements FeedJob {
    private static final String OPENMRS_ENCOUNTER_FEED_NAME = "openmrs.encounter.feed.uri";
    private final Logger logger = Logger.getLogger(this.getClass());

    private FeedClient atomFeedClient;

    @Autowired
    public EncounterFailedFeedJob(EncounterFeedWorker encounterFeedWorker, AtomFeedClientFactory atomFeedClientFactory) {
        atomFeedClient = atomFeedClientFactory.get(OPENMRS_ENCOUNTER_FEED_NAME, encounterFeedWorker);
    }

    public EncounterFailedFeedJob() {
    }

    @Override
    public void process() {
        logger.info("Processing failed event.");
        atomFeedClient.processFailedEvents();
        logger.info("Completed processing failed event.");
    }
}
