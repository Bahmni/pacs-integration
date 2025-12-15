package org.bahmni.module.pacsintegration.services.impl;

import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.FhirImagingStudy;
import org.bahmni.module.pacsintegration.atomfeed.mappers.ImagingStudyMapper;
import org.bahmni.module.pacsintegration.services.OpenMRSService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImagingStudyServiceImplTest {

    @Mock
    private OpenMRSService openMRSService;

    @Mock
    private ImagingStudyMapper imagingStudyMapper;

    @InjectMocks
    private ImagingStudyServiceImpl imagingStudyService;

    private static final String ORDER_UUID = "order-uuid-123";
    private static final String PATIENT_UUID = "patient-uuid-456";
    private static final String LOCATION_UUID = "location-uuid-789";
    private static final String STUDY_INSTANCE_UID = "1.2.826.0.1.3680043.8.498.12345";
    private static final String DESCRIPTION = "Chest X-Ray";

    @Test
    public void shouldCreateImagingStudyWhenValidStudyInstanceUID() throws Exception {
        FhirImagingStudy fhirImagingStudy = new FhirImagingStudy();
        when(imagingStudyMapper.buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION))
                .thenReturn(fhirImagingStudy);

        imagingStudyService.createImagingStudy(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);

        verify(imagingStudyMapper).buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);
        verify(openMRSService).createFhirImagingStudy(fhirImagingStudy);
    }

    @Test
    public void shouldNotCreateImagingStudyWhenStudyInstanceUIDIsNull() throws Exception {
        imagingStudyService.createImagingStudy(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, null, DESCRIPTION);

        verify(imagingStudyMapper, never()).buildFhirPayload(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(openMRSService, never()).createFhirImagingStudy(any(FhirImagingStudy.class));
    }

    @Test
    public void shouldNotCreateImagingStudyWhenStudyInstanceUIDIsEmpty() throws Exception {
        imagingStudyService.createImagingStudy(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, "", DESCRIPTION);

        verify(imagingStudyMapper, never()).buildFhirPayload(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(openMRSService, never()).createFhirImagingStudy(any(FhirImagingStudy.class));
    }

    @Test
    public void shouldNotCreateImagingStudyWhenStudyInstanceUIDIsWhitespace() throws Exception {
        imagingStudyService.createImagingStudy(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, "   ", DESCRIPTION);

        verify(imagingStudyMapper, never()).buildFhirPayload(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(openMRSService, never()).createFhirImagingStudy(any(FhirImagingStudy.class));
    }

    @Test
    public void shouldHandleExceptionWhenCreatingImagingStudyFails() throws Exception {
        FhirImagingStudy fhirImagingStudy = new FhirImagingStudy();
        when(imagingStudyMapper.buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION))
                .thenReturn(fhirImagingStudy);
        doThrow(new RuntimeException("Failed to create imaging study")).when(openMRSService).createFhirImagingStudy(fhirImagingStudy);

        imagingStudyService.createImagingStudy(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);

        verify(imagingStudyMapper).buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);
        verify(openMRSService).createFhirImagingStudy(fhirImagingStudy);
    }

    @Test
    public void shouldHandleExceptionWhenMapperFails() throws Exception {
        when(imagingStudyMapper.buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION))
                .thenThrow(new RuntimeException("Mapper failed"));

        imagingStudyService.createImagingStudy(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);

        verify(imagingStudyMapper).buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);
        verify(openMRSService, never()).createFhirImagingStudy(any(FhirImagingStudy.class));
    }
}
