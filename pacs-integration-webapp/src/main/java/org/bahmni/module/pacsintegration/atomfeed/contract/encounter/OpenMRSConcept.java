package org.bahmni.module.pacsintegration.atomfeed.contract.encounter;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSConcept {
    private String uuid;
    private OpenMRSConceptName name;
    private boolean set;
    private List<OpenMRSConceptMapping> mappings;


    public OpenMRSConcept() {
    }

    public OpenMRSConcept(String uuid, OpenMRSConceptName name, boolean set) {

        this.uuid = uuid;
        this.name = name;
        this.set = set;
    }

    public void setSet(boolean set) {
        this.set = set;
    }

    public List<OpenMRSConceptMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<OpenMRSConceptMapping> mappings) {
        this.mappings = mappings;
    }


    public String getUuid() {
        return uuid;
    }

    public OpenMRSConceptName getName() {
        return name;
    }

    public void setName(OpenMRSConceptName name) {
        this.name = name;
    }

    public boolean isSet() {
        return set;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OpenMRSConcept that = (OpenMRSConcept) o;

        if (!uuid.equals(that.uuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
