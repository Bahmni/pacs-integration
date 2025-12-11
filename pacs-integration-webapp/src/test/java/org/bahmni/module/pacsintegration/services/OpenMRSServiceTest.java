package org.bahmni.module.pacsintegration.services;

import junit.framework.Assert;
import org.bahmni.module.pacsintegration.atomfeed.*;
import org.bahmni.module.pacsintegration.atomfeed.client.*;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.*;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.contract.patient.*;
import org.bahmni.webclients.*;
import org.junit.*;
import org.junit.Test;
import org.junit.runner.*;
import org.mockito.Mock;
import org.powermock.api.mockito.*;
import org.powermock.core.classloader.annotations.*;
import org.powermock.modules.junit4.*;

import java.io.*;
import java.net.*;
import java.text.*;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

@PrepareForTest(WebClientFactory.class)
@RunWith(PowerMockRunner.class)
public class OpenMRSServiceTest extends OpenMRSMapperBaseTest {

    @Mock
    private HttpClient webClient;

    @Mock
    private org.bahmni.webclients.ConnectionDetails connectionDetails;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void ShouldGetEncounter() throws Exception{
        PowerMockito.mockStatic(WebClientFactory.class);
        when(WebClientFactory.getClient()).thenReturn(webClient);
        when(webClient.get(new URI("http://localhost:8050/encounter/1"))).thenReturn(new OpenMRSMapperBaseTest().deserialize("/sampleOpenMRSEncounter.json"));

        when(webClient.get(any(URI.class))).thenReturn(deserialize("/sampleOpenMRSEncounter.json"));
        when(connectionDetails.getAuthUrl()).thenReturn("urlPrefix");

        OpenMRSEncounter encounter = new OpenMRSService().getEncounter("/encounter/1");

        assertEquals("7820b07d-50e9-4fed-b991-c38692b3d4ec", encounter.getEncounterUuid());
    }

    @Test
    public void shouldGetPatient() throws Exception {
        PowerMockito.mockStatic(WebClientFactory.class);
        when(WebClientFactory.getClient()).thenReturn(webClient);
        String patientUuid = "105059a8-5226-4b1f-b512-0d3ae685287d";
        String identifier = "GAN200053";
        when(webClient.get(new URI("http://localhost:8050/openmrs/ws/rest/v1/patient/" + patientUuid+"?v=full"))).thenReturn(new OpenMRSMapperBaseTest().deserialize("/samplePatient.json"));

        when(connectionDetails.getAuthUrl()).thenReturn("urlPrefix");

        OpenMRSPatient patient = new OpenMRSService().getPatient(patientUuid);

        assertEquals(identifier, patient.getPatientId());

    }

    @Test
    public void shouldGetOrderDetails() throws Exception {
        PowerMockito.mockStatic(WebClientFactory.class);

        when(WebClientFactory.getClient()).thenReturn(webClient);
        when(connectionDetails.getAuthUrl()).thenReturn("http://localhost:8050");

        String orderUuid = "c661277e-1e40-4a47-96b2-bb9987d7f296";
        OpenMRSOrderDetails mockOrderDetails = new OpenMRSOrderDetails();
        mockOrderDetails.setUuid(orderUuid);

        when(webClient.get(anyString(), eq(OpenMRSOrderDetails.class))).thenReturn(mockOrderDetails);

        OpenMRSOrderDetails orderDetails = new OpenMRSService().getOrderDetails(orderUuid);

        assertNotNull(orderDetails);
        assertEquals(orderUuid, orderDetails.getUuid());
        verify(webClient).get(anyString(), eq(OpenMRSOrderDetails.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenOrderUuidIsNull() throws Exception {
        new OpenMRSService().getOrderDetails(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenOrderUuidIsEmpty() throws Exception {
        new OpenMRSService().getOrderDetails("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenOrderUuidIsBlank() throws Exception {
        new OpenMRSService().getOrderDetails("   ");
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenWebClientFails() throws Exception {
        PowerMockito.mockStatic(WebClientFactory.class);

        when(WebClientFactory.getClient()).thenReturn(webClient);
        when(connectionDetails.getAuthUrl()).thenReturn("http://localhost:8050");

        String orderUuid = "c661277e-1e40-4a47-96b2-bb9987d7f296";
        when(webClient.get(anyString(), eq(OpenMRSOrderDetails.class)))
                .thenThrow(new IOException("Network error"));

        new OpenMRSService().getOrderDetails(orderUuid);
    }
}