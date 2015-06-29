
package org.bahmni.pacsintegration.atomfeed.worker;

import org.apache.log4j.Logger;
import org.bahmni.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.pacsintegration.atomfeed.mappers.OpenMRSEncounterMapper;
import org.bahmni.pacsintegration.atomfeed.services.OpenMRSEncounterService;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.ObjectMapperRepository;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Component
public class EncounterFeedWorker implements EventWorker {

    private HttpClient webClient;
    private String urlPrefix;
    private OpenMRSEncounterService openMRSEncounterService;
    private static Logger logger = Logger.getLogger(EncounterFeedWorker.class);

    public EncounterFeedWorker(HttpClient authenticatedWebClient, String urlPrefix, OpenMRSEncounterService openMRSEncounterService) {
        this.webClient = authenticatedWebClient;
        this.urlPrefix = urlPrefix;
        this.openMRSEncounterService = openMRSEncounterService;
    }

    public EncounterFeedWorker() {
    }

    @Override
    public void process(Event event) {
        try {
            logger.info("Getting encounter data...");
            String content = event.getContent();
            String encounterJSON = webClient.get(URI.create(urlPrefix + content));
            OpenMRSEncounter openMRSEncounter = new OpenMRSEncounterMapper(ObjectMapperRepository.objectMapper).map(encounterJSON);

            openMRSEncounterService.save(openMRSEncounter);
        } catch (IOException e) {
            logger.error("Failed to parse encounter json.");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public void cleanUp(Event event) {
    }
}
