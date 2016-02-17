package org.bahmni.module.pacsintegration.atomfeed.worker;

import org.bahmni.module.pacsintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.pacsintegration.services.OpenMRSService;
import org.bahmni.module.pacsintegration.services.PacsIntegrationService;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class EncounterFeedWorkerTest extends OpenMRSMapperBaseTest {

    @Mock
    private PacsIntegrationService pacsIntegrationService;

    @Mock
    private OpenMRSService openMRSService;

    @InjectMocks
    private EncounterFeedWorker encounterFeedWorker = new EncounterFeedWorker();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldGetEncounterDataFromTheEventContentAndSaveIt() throws Exception {
        String content = "/openmrs/encounter/uuid1";
        OpenMRSOrder order = new OpenMRSOrder();
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.addTestOrder(order);
        when(openMRSService.getEncounter(content)).thenReturn(openMRSEncounter);

        encounterFeedWorker.process(new Event("event id", content));

        verify(pacsIntegrationService, times(1)).processEncounter(openMRSEncounter);
    }

    @Test
    public void shouldNotProcessEncounterIfNoOrdersInIt() throws Exception {
        String content = "/openmrs/encounter/uuid1";
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        when(openMRSService.getEncounter(content)).thenReturn(openMRSEncounter);

        encounterFeedWorker.process(new Event("event id", content));

        verify(pacsIntegrationService, times(0)).processEncounter(openMRSEncounter);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfJsonParseFails() throws Exception {
        String content = "something";
        when(openMRSService.getEncounter(content)).thenThrow(new IOException("Incorrect JSON"));

        encounterFeedWorker.process(new Event("event id", content));
    }
}