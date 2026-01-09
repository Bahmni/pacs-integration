package org.bahmni.module.pacsintegration.services;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.pacsintegration.atomfeed.client.ConnectionDetails;
import org.bahmni.module.pacsintegration.atomfeed.client.WebClientFactory;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderQueryBuilder;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderLocation;
import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.FhirImagingStudy;
import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.JsonPatchOperation;
import org.bahmni.module.pacsintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.pacsintegration.atomfeed.mappers.OpenMRSEncounterMapper;
import org.bahmni.module.pacsintegration.atomfeed.mappers.OpenMRSPatientMapper;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.HttpHeaders;
import org.bahmni.webclients.ObjectMapperRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Component
public class OpenMRSService {

    private static final Logger logger = LoggerFactory.getLogger(OpenMRSService.class);

    // REST URL Constants
    private static final String PATIENT_REST_URL = "/openmrs/ws/rest/v1/patient/";
    private static final String ORDER_REST_URL = "/openmrs/ws/rest/v1/order/";
    private static final String VISIT_LOCATION_REST_URL = "/openmrs/ws/rest/v1/bahmnicore/visitLocation/";
    private static final String LOCATION_REST_URL = "/openmrs/ws/rest/v1/location/";
    private static final String IMAGING_STUDY_REST_URL = "/openmrs/ws/fhir2/R4/ImagingStudy";

    // FHIR Bundle Constants
    private static final String UUID_KEY = "uuid";

    // Query Parameters
    private static final String PATIENT_VERSION_PARAM = "?v=full";
    private static final String LOCATION_VERSION_PARAM = "?v=custom:(uuid,display,name,tags:(display))";

    public OpenMRSEncounter getEncounter(String encounterUrl) throws IOException {
        try {
            HttpClient webClient = WebClientFactory.getClient();
            String urlPrefix = getURLPrefix();
            String encounterJSON = webClient.get(URI.create(urlPrefix + encounterUrl));
            return new OpenMRSEncounterMapper(ObjectMapperRepository.objectMapper).map(encounterJSON);
        } catch (IOException e) {
            logger.error("Failed to fetch encounter from URL: {}", encounterUrl, e);
            throw e;
        }
    }

    public OpenMRSPatient getPatient(String patientUuid) throws IOException, ParseException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();

        try {
            String patientJSON = webClient.get(URI.create(urlPrefix + PATIENT_REST_URL + patientUuid + PATIENT_VERSION_PARAM));
            return new OpenMRSPatientMapper().map(patientJSON);
        } catch (IOException e) {
            logger.error("Failed to fetch patient with UUID: {}", patientUuid, e);
            throw e;
        }
    }

    public OpenMRSOrderDetails getOrderDetails(String orderUuid) throws IOException {
        if (StringUtils.isBlank(orderUuid)) {
            throw new IllegalArgumentException("Order UUID cannot be null or empty");
        }

        logger.debug("Fetching order details for UUID: {}", orderUuid);
        try {
            HttpClient webClient = WebClientFactory.getClient();
            String urlPrefix = getURLPrefix();
            String url = urlPrefix + ORDER_REST_URL + orderUuid + "?" + OpenMRSOrderQueryBuilder.ORDER_DETAILS_QUERY_PARAM;
            return webClient.get(url, OpenMRSOrderDetails.class);
        } catch (IOException e) {
            throw new IOException("Failed to fetch order details for UUID: " + orderUuid + ". " + e.getMessage(), e);
        }
    }

    public String getVisitLocation(String locationUuid) throws IOException {
        if (StringUtils.isBlank(locationUuid)) {
            throw new IllegalArgumentException("Location UUID cannot be null or empty");
        }
        logger.debug("Fetching visit location for UUID: {}", locationUuid);
        try {
            HttpClient webClient = WebClientFactory.getClient();
            String urlPrefix = getURLPrefix();
            String url = urlPrefix + VISIT_LOCATION_REST_URL + locationUuid;
            Map<String, Object> response = webClient.get(url, Map.class);
            return (String) response.get(UUID_KEY);
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
        String url = urlPrefix + LOCATION_REST_URL + locationUuid + LOCATION_VERSION_PARAM;
        return webClient.get(url, OrderLocation.class);
    }

    public FhirImagingStudy createFhirImagingStudy(FhirImagingStudy payload) throws IOException {
        try {
            HttpClient webClient = WebClientFactory.getClient();
            String urlPrefix = getURLPrefix();
            String url = urlPrefix + IMAGING_STUDY_REST_URL;
            return webClient.post(url, payload, FhirImagingStudy.class);
        } catch (IOException e) {
            throw new IOException("Failed to create FHIR Imaging study", e);
        }
    }

    public void updateFhirImagingStudyStatus(String imagingStudyId, List<JsonPatchOperation> patchOperations) throws IOException {
        if (StringUtils.isBlank(imagingStudyId)) {
            throw new IllegalArgumentException("ImagingStudy ID cannot be null or empty");
        }
        if (patchOperations == null || patchOperations.isEmpty()) {
            throw new IllegalArgumentException("Patch operations cannot be null or empty");
        }
        try {
            String urlPrefix = getURLPrefix();
            String url = urlPrefix + IMAGING_STUDY_REST_URL + "/" + imagingStudyId;
            HttpClient webClient = WebClientFactory.getClient();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.put("Content-Type", "application/json-patch+json");
            webClient.patch(url, patchOperations, Object.class, httpHeaders);
        } catch (IOException e) {
            logger.error("Failed to update FHIR ImagingStudy status for ID: {}", imagingStudyId, e);
            throw e;
        }
    }

    private String getURLPrefix() {
        org.bahmni.webclients.ConnectionDetails connectionDetails = ConnectionDetails.get();
        String authenticationURI = connectionDetails.getAuthUrl();

        URL openMRSAuthURL;
        try {
            openMRSAuthURL = new URL(authenticationURI);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Is not a valid URI - " + authenticationURI, e);
        }
        return String.format("%s://%s", openMRSAuthURL.getProtocol(), openMRSAuthURL.getAuthority());
    }
}
