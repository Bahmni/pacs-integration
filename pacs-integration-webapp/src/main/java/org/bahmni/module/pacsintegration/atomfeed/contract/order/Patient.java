package org.bahmni.module.pacsintegration.atomfeed.contract.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient {
    private String uuid;
    private PatientIdentifier patientIdentifier;
    private Person person;

    @Data
    public static class PatientIdentifier {
        private String identifier;
    }
}

