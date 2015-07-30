package org.bahmni.module.pacsintegration.atomfeed.mappers;

import org.bahmni.module.pacsintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.webclients.ObjectMapperRepository;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class OpenMRSPatientMapper {
    private ObjectMapper objectMapper;
    private SimpleDateFormat dateOfBirthFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public OpenMRSPatientMapper() {
        this.objectMapper = ObjectMapperRepository.objectMapper;
    }

    public OpenMRSPatient map(String patientJSON) throws IOException, ParseException {
        OpenMRSPatient patient = new OpenMRSPatient();
        JsonNode jsonNode = objectMapper.readTree(patientJSON);

        patient.setPatientId(jsonNode.path("identifiers").get(0).path("identifier").asText());
        patient.setGivenName(jsonNode.path("person").path("preferredName").path("givenName").asText().replaceAll("[\\W&&[^-]]", " "));
        patient.setFamilyName(jsonNode.path("person").path("preferredName").path("familyName").asText().replaceAll("[\\W&&[^-]]", " "));
        patient.setGender(jsonNode.path("person").path("gender").asText());
        patient.setBirthDate(dateOfBirthFormat.parse(jsonNode.path("person").path("birthdate").asText()));

        return patient;
    }

}
