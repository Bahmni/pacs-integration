package org.bahmni.module.pacsintegration.atomfeed.mappers;

import org.bahmni.module.pacsintegration.atomfeed.contract.patient.CareContext;
import org.bahmni.module.pacsintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.pacsintegration.atomfeed.worker.EncounterFeedWorker;
import org.bahmni.webclients.ObjectMapperRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class OpenMRSPatientMapper {
    private ObjectMapper objectMapper;
    private SimpleDateFormat dateOfBirthFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private Logger logger = LoggerFactory.getLogger(EncounterFeedWorker.class);

    public OpenMRSPatientMapper() {
        this.objectMapper = ObjectMapperRepository.objectMapper;
    }

    public OpenMRSPatient map(String patientJSON) throws IOException, ParseException {
        OpenMRSPatient patient = new OpenMRSPatient();
        JsonNode jsonNode = objectMapper.readTree(patientJSON);

        patient.setPatientId(jsonNode.path("identifiers").get(0).path("identifier").asText());
        patient.setGivenName(jsonNode.path("person").path("preferredName").path("givenName").asText().replaceAll("[\\W&&[^-]]", " "));
        patient.setFamilyName(jsonNode.path("person").path("preferredName").path("familyName").asText().replaceAll("[\\W&&[^-]]", " "));
        patient.setMiddleName(jsonNode.path("person").path("preferredName").path("middleName").asText().replaceAll("[\\W&&[^-]]", " "));
        patient.setGender(jsonNode.path("person").path("gender").asText());
        patient.setBirthDate(dateOfBirthFormat.parse(jsonNode.path("person").path("birthdate").asText()));

        JsonNode personAttributes = jsonNode.path("person").path("attributes");
        for(JsonNode attributes : personAttributes){
            if(attributes.path("attributeType").path("display").asText().replaceAll("[\\W&&[^-]]", " ").equals("primaryContact")){
                patient.setPhoneNumber(attributes.path("value").asText().replaceAll("[\\W&&[^-]]", " "));
            }
        }
        return patient;
    }

    public OpenMRSPatient mapCareContext(String patientJSON) throws IOException, ParseException {
        OpenMRSPatient patient = new OpenMRSPatient();
        JsonNode jsonNode = objectMapper.readTree(patientJSON);

        patient.setHealthId(jsonNode.path("healthId").asText());
        patient.setPatientReferenceNumber(jsonNode.path("patientReferenceNumber").asText().replaceAll("[\\W&&[^-]]", " "));

        List<CareContext> careContexts = new ArrayList<CareContext>();
        JsonNode patientCareContexts = jsonNode.path("careContexts");
        for(JsonNode patientCareContext : patientCareContexts){
            CareContext careContext = new CareContext();
            careContext.setDisplay(patientCareContext.path("careContextName").asText().replaceAll("[\\W&&[^-]]", " "));
            careContext.setType(patientCareContext.path("careContextType").asText().replaceAll("[\\W&&[^-]]", " "));
            careContext.setReferenceNumber(patientCareContext.path("careContextReference").asInt());
            logger.warn("careContext" + careContext.getDisplay() + careContext.getType() + careContext.getReferenceNumber());
            careContexts.add(careContext);
        }
        patient.setCareContexts(careContexts);

        return patient;
    }

}
