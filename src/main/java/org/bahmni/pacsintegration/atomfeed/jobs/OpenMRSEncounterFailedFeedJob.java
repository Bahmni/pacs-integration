package org.bahmni.pacsintegration.atomfeed.jobs;

import org.apache.log4j.Logger;
import org.bahmni.pacsintegration.atomfeed.client.AtomFeedClientFactory;
import org.bahmni.pacsintegration.atomfeed.client.AtomFeedProperties;
import org.bahmni.pacsintegration.atomfeed.worker.EncounterFeedWorker;
import org.bahmni.webclients.ClientCookies;
import org.bahmni.webclients.ConnectionDetails;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.openmrs.OpenMRSLoginAuthenticator;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@DisallowConcurrentExecution
@Component("openMRSEncounterFailedFeedJob")
public class OpenMRSEncounterFailedFeedJob extends OpenMRSFeedJob {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private AtomFeedClientFactory atomFeedClientFactory;

    @Override
    String getFeedName() {
        return OPENMRS_ENCOUNTER_FEED_NAME;
    }

    @Override
    EventWorker createWorker(HttpClient authenticatedWebClient, String urlPrefix) {
        return new EncounterFeedWorker(authenticatedWebClient, urlPrefix);
    }

    @Override
    public void process() {
        logger.info("Processing failed events.");
        FeedClient atomFeedClient = createAtomFeedClient(AtomFeedProperties.getInstance(), atomFeedClientFactory);
        atomFeedClient.processFailedEvents();
    }
}
