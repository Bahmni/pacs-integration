package org.bahmni.module.pacsintegration.atomfeed.contract.order;

import lombok.Data;

import java.util.Date;

@Data
public class Person {
    private String gender;
    private Integer age;
    private Date birthdate;
    private PreferredName preferredName;


    @Data
    public static class PreferredName {
        private String givenName;
        private String middleName;
        private String familyName;
    }
}

