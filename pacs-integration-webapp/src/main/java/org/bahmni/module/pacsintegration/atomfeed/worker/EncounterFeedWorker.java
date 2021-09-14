
package org.bahmni.module.pacsintegration.atomfeed.worker;

import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.pacsintegration.services.OpenMRSService;
import org.bahmni.module.pacsintegration.services.PacsIntegrationService;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EncounterFeedWorker implements EventWorker {

    private static final Logger logger = LoggerFactory.getLogger(EncounterFeedWorker.class);

    @Autowired
    private PacsIntegrationService pacsIntegrationService;

    @Autowired
    private OpenMRSService openMRSService;

    public EncounterFeedWorker() {
    }

    @Override
    public void process(Event event) {
        String bedAssignment = "Bed-Assignment";
        try {
            if (event.getTitle() == null || !event.getTitle().equals(bedAssignment)) {
                logger.warn("Getting encounter data...");
                String encounterUri = event.getContent();
                OpenMRSEncounter encounter = openMRSService.getEncounter(encounterUri);
                pacsIntegrationService.processEncounter(encounter);
            }
        } catch (Exception e) {
            logger.error("Failed to process encounter", e);
            throw new RuntimeException("Failed to process encounter", e);
        }
    }

    @Override
    public void cleanUp(Event event) {
    }
}
