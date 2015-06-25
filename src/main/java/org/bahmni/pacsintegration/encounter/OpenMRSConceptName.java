package org.bahmni.pacsintegration.encounter;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSConceptName {
    private String name;

    public OpenMRSConceptName() {
    }

    public OpenMRSConceptName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
