package org.bahmni.module.pacsintegration.client;

import org.bahmni.webclients.ConnectionDetails;
import org.bahmni.webclients.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Dcm4CheeConnectionDetails.class)
public class HttpClientFactoryTest {

    private ConnectionDetails mockConnectionDetails;

    @Before
    public void setUp() {
        mockConnectionDetails = mock(ConnectionDetails.class);
        PowerMockito.mockStatic(Dcm4CheeConnectionDetails.class);
    }

    @Test
    public void shouldReturnNonNullHttpClient() {
        PowerMockito.when(Dcm4CheeConnectionDetails.get()).thenReturn(mockConnectionDetails);

        HttpClient httpClient = HttpClientFactory.getDcm4CheeClient();

        assertNotNull(httpClient);
    }

    @Test
    public void shouldCreateHttpClientWithConnectionDetails() {
        PowerMockito.when(Dcm4CheeConnectionDetails.get()).thenReturn(mockConnectionDetails);

        HttpClient httpClient = HttpClientFactory.getDcm4CheeClient();

        assertNotNull(httpClient);
        PowerMockito.verifyStatic();
    }

    @Test
    public void shouldReturnNewHttpClientInstanceOnEachCall() {
        PowerMockito.when(Dcm4CheeConnectionDetails.get()).thenReturn(mockConnectionDetails);

        HttpClient httpClient1 = HttpClientFactory.getDcm4CheeClient();
        HttpClient httpClient2 = HttpClientFactory.getDcm4CheeClient();

        assertNotNull(httpClient1);
        assertNotNull(httpClient2);
        assertNotSame(httpClient1, httpClient2);
    }
}
