package org.bahmni.pacsintegration.atomfeed.jobs;

import org.bahmni.pacsintegration.atomfeed.client.AtomFeedClientFactory;
import org.bahmni.pacsintegration.atomfeed.client.AtomFeedProperties;
import org.bahmni.webclients.ClientCookies;
import org.bahmni.webclients.ConnectionDetails;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.openmrs.OpenMRSLoginAuthenticator;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.client.service.FeedClient;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class OpenMRSFeedJob {
    static final String AUTH_URI = "openmrs.auth.uri";
    static final String OPENMRS_USER = "openmrs.user";
    static final String OPENMRS_PASSWORD = "openmrs.password";
    static final String OPENMRS_WEBCLIENT_CONNECT_TIMEOUT = "openmrs.connectionTimeoutInMilliseconds";
    static final String OPENMRS_WEBCLIENT_READ_TIMEOUT = "openmrs.replyTimeoutInMilliseconds";
    static final String OPENMRS_ENCOUNTER_FEED_NAME = "openmrs.encounter.feed.uri";

    abstract String getFeedName();
    abstract EventWorker createWorker(HttpClient authenticatedWebClient, String urlPrefix);
    public abstract void process() throws InterruptedException;


    String getURLPrefix(String authenticationURI) {
        URL openMRSAuthURL;
        try {
            openMRSAuthURL = new URL(authenticationURI);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Is not a valid URI - " + authenticationURI);
        }
        return String.format("%s://%s", openMRSAuthURL.getProtocol(), openMRSAuthURL.getAuthority());
    }

    ConnectionDetails getConnectionDetails() {
        AtomFeedProperties atomFeedProperties = AtomFeedProperties.getInstance();
        return new ConnectionDetails(
                atomFeedProperties.getProperty(AUTH_URI),
                atomFeedProperties.getProperty(OPENMRS_USER),
                atomFeedProperties.getProperty(OPENMRS_PASSWORD),
                Integer.parseInt(atomFeedProperties.getProperty(OPENMRS_WEBCLIENT_CONNECT_TIMEOUT)),
                Integer.parseInt(atomFeedProperties.getProperty(OPENMRS_WEBCLIENT_READ_TIMEOUT)));
    }

    OpenMRSLoginAuthenticator getAuthenticator(ConnectionDetails connectionDetails) {
        return new OpenMRSLoginAuthenticator(connectionDetails);

    }

    ClientCookies getCookies(HttpClient authenticatedWebClient, String urlString) {
        try {
            return authenticatedWebClient.getCookies(new URI(urlString));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Is not a valid URI - " + urlString);
        }
    }

    FeedClient createAtomFeedClient(AtomFeedProperties atomFeedProperties, AtomFeedClientFactory atomFeedClientFactory) {
        ConnectionDetails connectionDetails = getConnectionDetails();

        String authUri = connectionDetails.getAuthUrl();
        String urlString = getURLPrefix(authUri);

        HttpClient authenticatedWebClient = new HttpClient(connectionDetails, getAuthenticator(connectionDetails));

        ClientCookies cookies = getCookies(authenticatedWebClient, authUri);
        return atomFeedClientFactory.getFeedClient(atomFeedProperties,
                getFeedName(), createWorker(authenticatedWebClient, urlString), cookies);
    }
}
