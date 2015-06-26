package org.bahmni.pacsintegration.atomfeed.mappers;

import org.bahmni.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class OpenMRSEncounterMapper {
    private ObjectMapper objectMapper;

    public OpenMRSEncounterMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OpenMRSEncounter map(String encounterJSON) throws IOException {
        return objectMapper.readValue(encounterJSON, OpenMRSEncounter.class);
    }
}
