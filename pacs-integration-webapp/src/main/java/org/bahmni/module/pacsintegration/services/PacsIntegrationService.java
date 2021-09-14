package org.bahmni.module.pacsintegration.services;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.llp.LLPException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.pacsintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.pacsintegration.atomfeed.worker.EncounterFeedWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;


@Component
public class PacsIntegrationService {

    @Autowired
    private OpenMRSService openMRSService;

    private static final Logger logger = LoggerFactory.getLogger(EncounterFeedWorker.class);

    public void processEncounter(OpenMRSEncounter openMRSEncounter) throws IOException, ParseException, HL7Exception, LLPException {
        OpenMRSPatient patient = openMRSService.getPatient(openMRSEncounter.getPatientUuid());
        OpenMRSPatient patientCareContext = openMRSService.getCareContext(openMRSEncounter.getPatientUuid());

        logger.warn("Name :" + patient.getGivenName());
        logger.warn("phno :" + patient.getPhoneNumber());
        logger.warn("careContext " + patientCareContext.getHealthId() + patientCareContext.getCareContexts() + patientCareContext.getPatientReferenceNumber());

        callNewContext(patient, patientCareContext);
        callSmsNotify(patient,patientCareContext);

    }

    private void callNewContext(OpenMRSPatient patient, OpenMRSPatient patientCareContext) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://ndhm-dev.bahmni-covid19.in/hiprovider/v0.5/hip/new-carecontext");

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(patientCareContext.getCareContexts());

        String jsonInputString = "{\"patientReferenceNumber\": \"" + patientCareContext.getPatientReferenceNumber() +
                "\",\n \"patientName\":\"" + patient.getGivenName() +
                "\",\n\"careContexts\" : " + jsonString +
                ",\n\"healthId\" : \"" + patientCareContext.getHealthId() + "\"}";

        StringEntity entity = new StringEntity(jsonInputString, "application/json", "UTF-8");
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("CORRELATION_ID", null);
        logger.warn("===========================request begin================================================");
        logger.warn("URI         : {}", httpPost.getURI());
        logger.warn("Method      : {}", httpPost.getMethod());
        logger.warn("Headers     : {}", httpPost.getAllHeaders());
        logger.warn("Request body: {}", jsonInputString);
        logger.warn("==========================request end================================================");

        CloseableHttpResponse response = client.execute(httpPost);
        logger.warn(String.valueOf(response.getStatusLine().getStatusCode()));
        client.close();
    }


    private void callSmsNotify(OpenMRSPatient patient,OpenMRSPatient patientCareContext) throws IOException {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://ndhm-dev.bahmni-covid19.in/hiprovider/v0.5/hip/patients/sms/notify");

        String jsonInputString = "{\"phoneNo\": \"" + patient.getPhoneNumber() + "\",\n \"receiverName\":\"" + patient.getGivenName() + "\",\n\"careContextInfo\" : \"" + patientCareContext.getCareContextInfo() + "\"}";

        StringEntity entity = new StringEntity(jsonInputString, "application/json", "UTF-8");
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("CORRELATION_ID", null);
        logger.warn("===========================request begin================================================");
        logger.warn("URI         : {}", httpPost.getURI());
        logger.warn("Method      : {}", httpPost.getMethod());
        logger.warn("Headers     : {}", httpPost.getAllHeaders());
        logger.warn("Request body: {}", jsonInputString);
        logger.warn("==========================request end================================================");

        CloseableHttpResponse response = client.execute(httpPost);
        logger.warn(String.valueOf(response.getStatusLine().getStatusCode()));
        client.close();
    }

}
