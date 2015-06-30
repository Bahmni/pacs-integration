package org.bahmni.pacsintegration.atomfeed.builders;

import org.bahmni.pacsintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.pacsintegration.atomfeed.contract.encounter.OpenMRSConceptName;

public class OpenMRSConceptBuilder {
    private OpenMRSConcept openMRSConcept;

    public OpenMRSConceptBuilder() {
        this.openMRSConcept = new OpenMRSConcept();
    }

    public OpenMRSConceptBuilder withUuid(String uuid) {
        openMRSConcept.setUuid(uuid);
        return this;
    }

    public OpenMRSConceptBuilder withName(OpenMRSConceptName conceptName) {
        openMRSConcept.setName(conceptName);
        return this;
    }

    public OpenMRSConcept build() {
        return openMRSConcept;
    }
}
