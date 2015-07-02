package org.bahmni.module.pacsintegration.atomfeed.builders;

import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConceptName;

public class OpenMRSConceptNameBuilder {
    private OpenMRSConceptName openMRSConceptName;

    public OpenMRSConceptNameBuilder() {
        this.openMRSConceptName = new OpenMRSConceptName();
    }

    public OpenMRSConceptNameBuilder withName(String name) {
        openMRSConceptName.setName(name);
        return this;
    }

    public OpenMRSConceptName build() {
        return openMRSConceptName;
    }
}