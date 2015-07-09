package org.bahmni.module.pacsintegration.atomfeed.builders;

import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConceptMapping;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConceptName;

import java.util.ArrayList;
import java.util.List;

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

    public OpenMRSConceptBuilder withConceptMappings(List<OpenMRSConceptMapping> conceptMappings) {
        openMRSConcept.setMappings(conceptMappings);
        return this;
    }

    public OpenMRSConceptBuilder addConceptMapping(OpenMRSConceptMapping mapping) {
        if(openMRSConcept.getMappings() == null) {
            openMRSConcept.setMappings(new ArrayList<OpenMRSConceptMapping>());
        }
        openMRSConcept.getMappings().add(mapping);
        return this;
    }

    public OpenMRSConcept build() {
        return openMRSConcept;
    }
}
