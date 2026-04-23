package org.bahmni.module.pacsintegration.services;

import org.bahmni.module.pacsintegration.client.HttpClientFactory;
import org.bahmni.module.pacsintegration.dto.DicomMetadataDTO;
import org.bahmni.webclients.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@PrepareForTest(HttpClientFactory.class)
@RunWith(PowerMockRunner.class)
public class Dcm4CheeServiceTest {

    @Mock
    private HttpClient webClient;

    private Dcm4CheeService dcm4CheeService;

    private static final String DCM4CHEE_BASE_URL = "http://localhost:8080";
    private static final String DCM4CHEE_AET = "DCM4CHEE";
    private static final String STUDY_INSTANCE_UID = "1.2.3.4.5.6.7.8.9";

    @Before
    public void setUp() throws Exception {
        dcm4CheeService = new Dcm4CheeService();
        ReflectionTestUtils.setField(dcm4CheeService, "dcm4cheeBaseUrl", DCM4CHEE_BASE_URL);
        ReflectionTestUtils.setField(dcm4CheeService, "dcm4cheeAet", DCM4CHEE_AET);
    }

    @Test
    public void shouldFetchStudyMetadataSuccessfully() throws Exception {
        PowerMockito.mockStatic(HttpClientFactory.class);
        when(HttpClientFactory.getDcm4CheeClient()).thenReturn(webClient);

        DicomMetadataDTO[] expectedMetadata = new DicomMetadataDTO[2];
        expectedMetadata[0] = new DicomMetadataDTO();
        expectedMetadata[1] = new DicomMetadataDTO();

        String expectedUrl = String.format("%s/dcm4chee-arc/aets/%s/rs/studies/%s/metadata",
                DCM4CHEE_BASE_URL, DCM4CHEE_AET, STUDY_INSTANCE_UID);

        when(webClient.get(expectedUrl, DicomMetadataDTO[].class)).thenReturn(expectedMetadata);

        DicomMetadataDTO[] result = dcm4CheeService.fetchStudyMetadata(STUDY_INSTANCE_UID);

        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals(expectedMetadata, result);
        verify(webClient).get(expectedUrl, DicomMetadataDTO[].class);
    }

    @Test
    public void shouldReturnEmptyArrayWhenNoMetadataFound() throws Exception {
        PowerMockito.mockStatic(HttpClientFactory.class);
        when(HttpClientFactory.getDcm4CheeClient()).thenReturn(webClient);

        DicomMetadataDTO[] expectedMetadata = new DicomMetadataDTO[0];
        String expectedUrl = String.format("%s/dcm4chee-arc/aets/%s/rs/studies/%s/metadata",
                DCM4CHEE_BASE_URL, DCM4CHEE_AET, STUDY_INSTANCE_UID);

        when(webClient.get(expectedUrl, DicomMetadataDTO[].class)).thenReturn(expectedMetadata);

        DicomMetadataDTO[] result = dcm4CheeService.fetchStudyMetadata(STUDY_INSTANCE_UID);

        assertNotNull(result);
        assertEquals(0, result.length);
        verify(webClient).get(expectedUrl, DicomMetadataDTO[].class);
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenFetchFails() throws Exception {
        PowerMockito.mockStatic(HttpClientFactory.class);
        when(HttpClientFactory.getDcm4CheeClient()).thenReturn(webClient);

        String expectedUrl = String.format("%s/dcm4chee-arc/aets/%s/rs/studies/%s/metadata",
                DCM4CHEE_BASE_URL, DCM4CHEE_AET, STUDY_INSTANCE_UID);

        when(webClient.get(expectedUrl, DicomMetadataDTO[].class))
                .thenThrow(new IOException("Network error"));

        dcm4CheeService.fetchStudyMetadata(STUDY_INSTANCE_UID);
    }

    @Test
    public void shouldBuildCorrectMetadataUrl() throws Exception {
        PowerMockito.mockStatic(HttpClientFactory.class);
        when(HttpClientFactory.getDcm4CheeClient()).thenReturn(webClient);

        DicomMetadataDTO[] expectedMetadata = new DicomMetadataDTO[0];
        String customStudyUID = "9.8.7.6.5.4.3.2.1";
        String expectedUrl = String.format("%s/dcm4chee-arc/aets/%s/rs/studies/%s/metadata",
                DCM4CHEE_BASE_URL, DCM4CHEE_AET, customStudyUID);

        when(webClient.get(expectedUrl, DicomMetadataDTO[].class)).thenReturn(expectedMetadata);

        dcm4CheeService.fetchStudyMetadata(customStudyUID);

        verify(webClient).get(expectedUrl, DicomMetadataDTO[].class);
    }

    @Test
    public void shouldBuildUrlWithDifferentBaseUrlAndAet() throws Exception {
        PowerMockito.mockStatic(HttpClientFactory.class);
        when(HttpClientFactory.getDcm4CheeClient()).thenReturn(webClient);

        String customBaseUrl = "https://pacs.example.com";
        String customAet = "CUSTOM_AET";
        ReflectionTestUtils.setField(dcm4CheeService, "dcm4cheeBaseUrl", customBaseUrl);
        ReflectionTestUtils.setField(dcm4CheeService, "dcm4cheeAet", customAet);

        DicomMetadataDTO[] expectedMetadata = new DicomMetadataDTO[0];
        String expectedUrl = String.format("%s/dcm4chee-arc/aets/%s/rs/studies/%s/metadata",
                customBaseUrl, customAet, STUDY_INSTANCE_UID);

        when(webClient.get(expectedUrl, DicomMetadataDTO[].class)).thenReturn(expectedMetadata);

        dcm4CheeService.fetchStudyMetadata(STUDY_INSTANCE_UID);

        verify(webClient).get(expectedUrl, DicomMetadataDTO[].class);
    }

    @Test
    public void shouldHandleNullResponseFromWebClient() throws Exception {
        PowerMockito.mockStatic(HttpClientFactory.class);
        when(HttpClientFactory.getDcm4CheeClient()).thenReturn(webClient);

        String expectedUrl = String.format("%s/dcm4chee-arc/aets/%s/rs/studies/%s/metadata",
                DCM4CHEE_BASE_URL, DCM4CHEE_AET, STUDY_INSTANCE_UID);

        when(webClient.get(expectedUrl, DicomMetadataDTO[].class)).thenReturn(null);

        DicomMetadataDTO[] result = dcm4CheeService.fetchStudyMetadata(STUDY_INSTANCE_UID);

        assertNull(result);
        verify(webClient).get(expectedUrl, DicomMetadataDTO[].class);
    }

    @Test
    public void shouldHandleStudyUIDWithSpecialCharacters() throws Exception {
        PowerMockito.mockStatic(HttpClientFactory.class);
        when(HttpClientFactory.getDcm4CheeClient()).thenReturn(webClient);

        String studyUIDWithSpecialChars = "1.2.840.10008.5.1.4.1.1.1.1";
        DicomMetadataDTO[] expectedMetadata = new DicomMetadataDTO[1];
        expectedMetadata[0] = new DicomMetadataDTO();

        String expectedUrl = String.format("%s/dcm4chee-arc/aets/%s/rs/studies/%s/metadata",
                DCM4CHEE_BASE_URL, DCM4CHEE_AET, studyUIDWithSpecialChars);

        when(webClient.get(expectedUrl, DicomMetadataDTO[].class)).thenReturn(expectedMetadata);

        DicomMetadataDTO[] result = dcm4CheeService.fetchStudyMetadata(studyUIDWithSpecialChars);

        assertNotNull(result);
        assertEquals(1, result.length);
        verify(webClient).get(expectedUrl, DicomMetadataDTO[].class);
    }
}
