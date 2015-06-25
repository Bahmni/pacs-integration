
package org.bahmni.pacsintegration.atomfeed.worker;

import org.apache.log4j.Logger;
import org.bahmni.pacsintegration.OpenMRSEncounterMapper;
import org.bahmni.pacsintegration.encounter.OpenMRSEncounter;
import org.bahmni.pacsintegration.encounter.OpenMRSOrder;
import org.bahmni.pacsintegration.model.Order;
import org.bahmni.pacsintegration.repository.OrderTypeRepository;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.ObjectMapperRepository;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

//analogous to a controller because it receives the request which is an event in this case
public class EncounterFeedWorker implements EventWorker {
    private HttpClient webClient;
    private String urlPrefix;


    private static long provider_requester_type_id;
    private static String referring_org_type_id;
    private static Logger logger = Logger.getLogger(EncounterFeedWorker.class);

    public EncounterFeedWorker(HttpClient webClient, String urlPrefix) {
        this.webClient = webClient;
        this.urlPrefix = urlPrefix;
    }

    @Override
    public void process(Event event) {
        try {
            String content = event.getContent();
            String encounterJSON = webClient.get(URI.create(urlPrefix + content));
            OpenMRSEncounterMapper openMRSEncounterMapper = new OpenMRSEncounterMapper(ObjectMapperRepository.objectMapper);
            OpenMRSEncounter openMRSEncounter = null;
            openMRSEncounter = openMRSEncounterMapper.map(encounterJSON);
            if (openMRSEncounter.hasLabOrder()) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanUp(Event event) {

    }
}
