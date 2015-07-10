package org.bahmni.module.pacsintegration.services;

import junit.framework.Assert;
import org.bahmni.module.pacsintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.module.pacsintegration.atomfeed.client.WebClientFactory;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.webclients.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.URI;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@PrepareForTest(WebClientFactory.class)
@RunWith(PowerMockRunner.class)
public class OpenMRSServiceTest extends OpenMRSMapperBaseTest{

    @Mock
    private HttpClient webClient;

    @Mock
    private org.bahmni.webclients.ConnectionDetails connectionDetails;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void ShouldGetEncounter() throws IOException {
        PowerMockito.mockStatic(WebClientFactory.class);
        when(WebClientFactory.getClient()).thenReturn(webClient);
        when(webClient.get(any(URI.class))).thenReturn(deserialize("/sampleOpenMRSEncounter.json"));
        when(connectionDetails.getAuthUrl()).thenReturn("urlPrefix");

        OpenMRSEncounter encounter = new OpenMRSService().getEncounter("/encounterUrl");

        Assert.assertEquals(encounter.getEncounterUuid(), "7820b07d-50e9-4fed-b991-c38692b3d4ec");
    }
}