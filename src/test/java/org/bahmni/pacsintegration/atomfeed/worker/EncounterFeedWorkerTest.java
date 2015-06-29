package org.bahmni.pacsintegration.atomfeed.worker;

import org.bahmni.pacsintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.pacsintegration.atomfeed.services.OpenMRSEncounterService;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.net.URI;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EncounterFeedWorkerTest extends OpenMRSMapperBaseTest {
    @Mock
    private HttpClient webClient;

    @Mock
    private OpenMRSEncounterService openMRSEncounterService;

    @InjectMocks
    private EncounterFeedWorker encounterFeedWorker = new EncounterFeedWorker();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldGetEncounterDataFromTheEventContentAndSaveIt() throws Exception {
        when(webClient.get(any(URI.class))).thenReturn(deserialize("/sampleOpenMRSEncounter.json"));

        encounterFeedWorker.process(new Event("event id", "/openmrs"));

        verify(webClient, times(1)).get(any(URI.class));
        verify(openMRSEncounterService, times(1)).save(any(OpenMRSEncounter.class));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfJsonParseFails() throws Exception {
        when(webClient.get(any(URI.class))).thenReturn("Incorrect JSON");

        encounterFeedWorker.process(new Event("event id", "/openmrs"));
    }
}