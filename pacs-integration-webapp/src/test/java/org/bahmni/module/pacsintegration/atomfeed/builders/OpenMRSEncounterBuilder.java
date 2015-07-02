package org.bahmni.module.pacsintegration.atomfeed.builders;

import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;

public class OpenMRSEncounterBuilder {
    private OpenMRSEncounter openMRSEncounter;

    public OpenMRSEncounterBuilder() {
        openMRSEncounter = new OpenMRSEncounter();
    }

    public OpenMRSEncounterBuilder withEncounterUuid(String encounterUuid) {
        openMRSEncounter.setEncounterUuid(encounterUuid);
        return this;
    }

    public OpenMRSEncounterBuilder withPatientUuid(String patientUuid) {
        openMRSEncounter.setPatientUuid(patientUuid);
        return this;
    }

    public OpenMRSEncounterBuilder withTestOrder(OpenMRSOrder order) {
        openMRSEncounter.addTestOrder(order);
        return this;
    }

    public OpenMRSEncounter build() {
        return openMRSEncounter;
    }
}
