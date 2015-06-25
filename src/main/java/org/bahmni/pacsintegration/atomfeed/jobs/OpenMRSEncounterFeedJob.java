package org.bahmni.pacsintegration.atomfeed.jobs;

import org.bahmni.pacsintegration.atomfeed.client.AtomFeedClientFactory;
import org.bahmni.pacsintegration.atomfeed.client.AtomFeedProperties;
import org.bahmni.pacsintegration.atomfeed.worker.EncounterFeedWorker;
import org.bahmni.webclients.ClientCookies;
import org.bahmni.webclients.ConnectionDetails;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.openmrs.OpenMRSLoginAuthenticator;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Component("openMRSEncounterFeedJob")
public class OpenMRSEncounterFeedJob implements Job {

    private static final String AUTH_URI = "openmrs.auth.uri";
    private static final String OPENMRS_USER = "openmrs.user";
    private static final String OPENMRS_PASSWORD = "openmrs.password";
    private static final String OPENMRS_WEBCLIENT_CONNECT_TIMEOUT = "openmrs.connectionTimeoutInMilliseconds";
    private static final String OPENMRS_WEBCLIENT_READ_TIMEOUT = "openmrs.replyTimeoutInMilliseconds";


    private String feedName = "saleorder.feed.generator.uri";
    public static final String OPENMRS_ENCOUNTER_FEED_NAME = "openmrs.encounter.feed.uri";

    @Autowired
    private AtomFeedClientFactory atomFeedClientFactory;

    public OpenMRSEncounterFeedJob() {
    }

    @Override
    public void process() {
        //Create Atom Feed Clinet
        System.out.println("Processing... feed");
        FeedClient atomFeedClient = createAtomFeedClient(AtomFeedProperties.getInstance(), atomFeedClientFactory);
        atomFeedClient.processEvents();
        System.out.println("Processing... end.. No exp");


//        if (atomFeedClient != null) {
//            atomFeedClients.put(this.getClass(), atomFeedClient);
//        }
//        openMRSFeedJob.processFeed(feedName, Jobs.ENCOUNTER_FEED);
    }

    private FeedClient createAtomFeedClient(AtomFeedProperties atomFeedProperties, AtomFeedClientFactory atomFeedClientFactory) {
        ConnectionDetails connectionDetails = getConnectionDetails();

        String authUri = connectionDetails.getAuthUrl();
        String urlString = getURLPrefix(authUri);

        HttpClient authenticatedWebClient = new HttpClient(connectionDetails, getAuthenticator(connectionDetails));

        ClientCookies cookies = getCookies(authenticatedWebClient, authUri);
        return atomFeedClientFactory.getFeedClient(atomFeedProperties,
                getFeedName(), createWorker(authenticatedWebClient, urlString), cookies);
    }

    private ClientCookies getCookies(HttpClient authenticatedWebClient, String urlString) {
        try {
            return authenticatedWebClient.getCookies(new URI(urlString));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Is not a valid URI - " + urlString);
        }
    }

    protected OpenMRSLoginAuthenticator getAuthenticator(ConnectionDetails connectionDetails) {
        return new OpenMRSLoginAuthenticator(connectionDetails);
    }

    protected EventWorker createWorker(HttpClient authenticatedWebClient, String urlPrefix) {
        return new EncounterFeedWorker(authenticatedWebClient, urlPrefix);
    }

    protected String getFeedName() {
        return OPENMRS_ENCOUNTER_FEED_NAME;
    }


    private ConnectionDetails getConnectionDetails() {
        AtomFeedProperties atomFeedProperties = AtomFeedProperties.getInstance();
        return new ConnectionDetails(
                atomFeedProperties.getProperty(AUTH_URI),
                atomFeedProperties.getProperty(OPENMRS_USER),
                atomFeedProperties.getProperty(OPENMRS_PASSWORD),
                Integer.parseInt(atomFeedProperties.getProperty(OPENMRS_WEBCLIENT_CONNECT_TIMEOUT)),
                Integer.parseInt(atomFeedProperties.getProperty(OPENMRS_WEBCLIENT_READ_TIMEOUT)));
    }
    private static String getURLPrefix(String authenticationURI) {
        URL openMRSAuthURL;
        try {
            openMRSAuthURL = new URL(authenticationURI);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Is not a valid URI - " + authenticationURI);
        }
        return String.format("%s://%s", openMRSAuthURL.getProtocol(), openMRSAuthURL.getAuthority());
    }

}
