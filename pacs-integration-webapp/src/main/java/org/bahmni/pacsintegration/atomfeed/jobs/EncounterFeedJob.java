package org.bahmni.pacsintegration.atomfeed.jobs;

import org.apache.log4j.Logger;
import org.bahmni.pacsintegration.atomfeed.client.AtomFeedClientFactory;
import org.bahmni.pacsintegration.atomfeed.worker.EncounterFeedWorker;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@DisallowConcurrentExecution
@Component("openMRSEncounterFeedJob")
@ConditionalOnExpression("'${enable.scheduling}'=='true'")
public class EncounterFeedJob implements FeedJob {
    private static final String OPENMRS_ENCOUNTER_FEED_NAME = "openmrs.encounter.feed.uri";
    private final Logger logger = Logger.getLogger(this.getClass());
    private FeedClient atomFeedClient;

    @Autowired
    public EncounterFeedJob(EncounterFeedWorker encounterFeedWorker, AtomFeedClientFactory atomFeedClientFactory) {
        atomFeedClient = atomFeedClientFactory.get(OPENMRS_ENCOUNTER_FEED_NAME, encounterFeedWorker);
    }

    public EncounterFeedJob() {
    }

    @Override
    public void process() throws InterruptedException {
        logger.info("Processing feed...");
        atomFeedClient.processEvents();
        logger.info("Completed processing feed...");
    }
}
