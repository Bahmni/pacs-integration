package org.bahmni.pacsintegration.atomfeed.jobs;

import org.apache.log4j.Logger;
import org.bahmni.pacsintegration.atomfeed.client.AtomFeedClientFactory;
import org.bahmni.pacsintegration.atomfeed.client.AtomFeedProperties;
import org.bahmni.pacsintegration.atomfeed.services.OpenMRSEncounterService;
import org.bahmni.pacsintegration.atomfeed.worker.EncounterFeedWorker;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@DisallowConcurrentExecution
@Component("openMRSEncounterFeedJob")
public class OpenMRSEncounterFeedJob extends OpenMRSFeedJob {
    @Autowired
    private AtomFeedClientFactory atomFeedClientFactory;
    @Autowired
    private OpenMRSEncounterService openMRSEncounterService;


    private final Logger logger = Logger.getLogger(this.getClass());

    public OpenMRSEncounterFeedJob() {
    }

    @Override
    public void process() throws InterruptedException {
        logger.info("Processing feed...");
        FeedClient atomFeedClient = createAtomFeedClient(AtomFeedProperties.getInstance(), atomFeedClientFactory);
        atomFeedClient.processEvents();
    }

    @Override
    EventWorker createWorker(HttpClient authenticatedWebClient, String urlPrefix) {
        return new EncounterFeedWorker(authenticatedWebClient, urlPrefix, openMRSEncounterService);
    }

    @Override
    String getFeedName() {
        return OPENMRS_ENCOUNTER_FEED_NAME;
    }
}
