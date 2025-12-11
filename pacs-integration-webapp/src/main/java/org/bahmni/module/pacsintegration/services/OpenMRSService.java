package org.bahmni.module.pacsintegration.services;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.pacsintegration.atomfeed.client.ConnectionDetails;
import org.bahmni.module.pacsintegration.atomfeed.client.WebClientFactory;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderQueryBuilder;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderLocation;
import org.bahmni.module.pacsintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.pacsintegration.atomfeed.mappers.OpenMRSEncounterMapper;
import org.bahmni.module.pacsintegration.atomfeed.mappers.OpenMRSPatientMapper;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.ObjectMapperRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.Map;

@Component
public class OpenMRSService {

    String patientRestUrl = "/openmrs/ws/rest/v1/patient/";
    String orderRestUrl = "/openmrs/ws/rest/v1/order/";
    String visitLocationRestUrl = "/openmrs/ws/rest/v1/bahmnicore/visitLocation/";
    String locationRestUrl = "/openmrs/ws/rest/v1/location/";

    public OpenMRSEncounter getEncounter(String encounterUrl) throws IOException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();

        String encounterJSON = webClient.get(URI.create(urlPrefix + encounterUrl));
        return new OpenMRSEncounterMapper(ObjectMapperRepository.objectMapper).map(encounterJSON);
    }

    public OpenMRSPatient getPatient(String patientUuid) throws IOException, ParseException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();

        String patientJSON = webClient.get(URI.create(urlPrefix + patientRestUrl + patientUuid+"?v=full"));
        return new OpenMRSPatientMapper().map(patientJSON);
    }

    public OpenMRSOrderDetails getOrderDetails(String orderUuid) throws IOException {
        if (StringUtils.isBlank(orderUuid)) {
            throw new IllegalArgumentException("Order UUID cannot be null or empty");
        }

        try {
            HttpClient webClient = WebClientFactory.getClient();
            String urlPrefix = getURLPrefix();
            String url = urlPrefix + orderRestUrl + orderUuid + "?" + OpenMRSOrderQueryBuilder.ORDER_DETAILS_QUERY_PARAM;

            return webClient.get(url, OpenMRSOrderDetails.class);
        } catch (IOException e) {
            throw new IOException("Failed to fetch order details for UUID: " + orderUuid + ". " + e.getMessage(), e);
        }
    }

    public String getVisitLocation(String locationUuid) throws IOException {
        if (StringUtils.isBlank(locationUuid)) {
            throw new IllegalArgumentException("Location UUID cannot be null or empty");
        }

        try {
            HttpClient webClient = WebClientFactory.getClient();
            String urlPrefix = getURLPrefix();
            String url = urlPrefix + visitLocationRestUrl + locationUuid;

            Map<String, Object> response = webClient.get(url, Map.class);
            return (String) response.get("uuid");
        } catch (IOException e) {
            throw new IOException("Failed to fetch visit location for UUID: " + locationUuid + ". " + e.getMessage(), e);
        }
    }

    public OrderLocation getLocation(String locationUuid) throws IOException {
        if (StringUtils.isBlank(locationUuid)) {
            throw new IllegalArgumentException("Location UUID cannot be null or empty");
        }

        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();
        String url = urlPrefix + locationRestUrl + locationUuid + "?v=custom:(uuid,display,name,tags:(display))";

        return webClient.get(url, OrderLocation.class);
    }

    private String getURLPrefix() {
        org.bahmni.webclients.ConnectionDetails connectionDetails = ConnectionDetails.get();
        String authenticationURI = connectionDetails.getAuthUrl();

        URL openMRSAuthURL;
        try {
            openMRSAuthURL = new URL(authenticationURI);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Is not a valid URI - " + authenticationURI);
        }
        return String.format("%s://%s", openMRSAuthURL.getProtocol(), openMRSAuthURL.getAuthority());
    }

}
