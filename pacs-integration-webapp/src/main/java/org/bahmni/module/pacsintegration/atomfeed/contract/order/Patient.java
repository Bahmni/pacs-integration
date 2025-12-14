package org.bahmni.module.pacsintegration.atomfeed.contract.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

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

    @Data
    public static class Person {
        private String gender;
        private Integer age;
        private Date birthdate;
        private PreferredName preferredName;
    }

    @Data
    public static class PreferredName {
        private String givenName;
        private String middleName;
        private String familyName;
    }
}

