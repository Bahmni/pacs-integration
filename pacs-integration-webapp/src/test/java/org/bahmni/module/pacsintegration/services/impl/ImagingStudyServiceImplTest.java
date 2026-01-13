package org.bahmni.module.pacsintegration.services.impl;

import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.FhirImagingStudy;
import org.bahmni.module.pacsintegration.atomfeed.mappers.ImagingStudyMapper;
import org.bahmni.module.pacsintegration.model.ImagingStudyReference;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.repository.ImagingStudyReferenceRepository;
import org.bahmni.module.pacsintegration.services.OpenMRSService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImagingStudyServiceImplTest  {

    @Mock
    private OpenMRSService openMRSService;

    @Mock
    private ImagingStudyMapper imagingStudyMapper;

    @Mock
    private ImagingStudyReferenceRepository imagingStudyReferenceRepository;

    @InjectMocks
    private ImagingStudyServiceImpl imagingStudyService;

    private static final String ORDER_UUID = "order-uuid-123";
    private static final String PATIENT_UUID = "patient-uuid-456";
    private static final String LOCATION_UUID = "location-uuid-789";
    private static final String STUDY_INSTANCE_UID = "1.2.826.0.1.3680043.8.498.12345";
    private static final String DESCRIPTION = "Chest X-Ray";

    @Test
    public void shouldCreateImagingStudyWhenValidStudyInstanceUID() throws Exception {
        String expectedUuid = "imaging-study-uuid-123";
        FhirImagingStudy fhirImagingStudy = new FhirImagingStudy();
        fhirImagingStudy.setId(expectedUuid);
        Order order = new Order();
        order.setId(1);
        order.setOrderUuid(ORDER_UUID);
        
        when(imagingStudyMapper.buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION))
                .thenReturn(fhirImagingStudy);
        when(openMRSService.createFhirImagingStudy(fhirImagingStudy)).thenReturn(fhirImagingStudy);

        String result = imagingStudyService.createImagingStudy(order, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);

        assertNotNull(result);
        assertEquals(expectedUuid, result);
        verify(imagingStudyMapper).buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);
        verify(openMRSService).createFhirImagingStudy(fhirImagingStudy);
        verify(imagingStudyReferenceRepository).save(any(ImagingStudyReference.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenStudyInstanceUIDIsNull() throws Exception {
        imagingStudyService.createImagingStudy(new Order(), PATIENT_UUID, LOCATION_UUID, null, DESCRIPTION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenStudyInstanceUIDIsEmpty() throws Exception {
        imagingStudyService.createImagingStudy(new Order(), PATIENT_UUID, LOCATION_UUID, "", DESCRIPTION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenStudyInstanceUIDIsWhitespace() throws Exception {
        imagingStudyService.createImagingStudy(new Order(), PATIENT_UUID, LOCATION_UUID, "   ", DESCRIPTION);
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenOpenMRSServiceFails() throws Exception {
        FhirImagingStudy fhirImagingStudy = new FhirImagingStudy();
        Order order = new Order();
        order.setOrderUuid(ORDER_UUID);
        when(imagingStudyMapper.buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION))
                .thenReturn(fhirImagingStudy);
        when(openMRSService.createFhirImagingStudy(fhirImagingStudy))
                .thenThrow(new IOException("Failed to create imaging study"));

        imagingStudyService.createImagingStudy(order, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenOpenMRSReturnsNullUuid() throws Exception {
        FhirImagingStudy fhirImagingStudy = new FhirImagingStudy();
        Order order = new Order();
        order.setOrderUuid(ORDER_UUID);
        when(imagingStudyMapper.buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION))
                .thenReturn(fhirImagingStudy);
        when(openMRSService.createFhirImagingStudy(fhirImagingStudy)).thenReturn(fhirImagingStudy);

        imagingStudyService.createImagingStudy(order, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenOpenMRSReturnsEmptyUuid() throws Exception {
        FhirImagingStudy fhirImagingStudy = new FhirImagingStudy();
        Order order = new Order();
        order.setOrderUuid(ORDER_UUID);
        when(imagingStudyMapper.buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION))
                .thenReturn(fhirImagingStudy);
        when(openMRSService.createFhirImagingStudy(fhirImagingStudy)).thenReturn(new FhirImagingStudy());

        imagingStudyService.createImagingStudy(order, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);
    }

    @Test
    public void shouldCreateImagingStudyAndSaveReference() throws Exception {
        String expectedUuid = "imaging-study-uuid-123";
        FhirImagingStudy fhirImagingStudy = new FhirImagingStudy();
        fhirImagingStudy.setId(expectedUuid);
        Order order = new Order();
        order.setId(1);
        order.setOrderUuid(ORDER_UUID);

        when(imagingStudyMapper.buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION))
                .thenReturn(fhirImagingStudy);
        when(openMRSService.createFhirImagingStudy(fhirImagingStudy)).thenReturn(fhirImagingStudy);

        String result = imagingStudyService.createImagingStudy(order, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);

        assertNotNull(result);
        assertEquals(expectedUuid, result);
        verify(imagingStudyMapper).buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);
        verify(openMRSService).createFhirImagingStudy(fhirImagingStudy);
        verify(imagingStudyReferenceRepository).save(any(ImagingStudyReference.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenUpdateStatusWithNullStudyInstanceUID() throws Exception {
        imagingStudyService.updateImagingStudyAsAvailable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenUpdateStatusWithEmptyStudyInstanceUID() throws Exception {
        imagingStudyService.updateImagingStudyAsAvailable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenUpdateStatusWithBlankStudyInstanceUID() throws Exception {
        imagingStudyService.updateImagingStudyAsAvailable("   ");
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenImagingStudyReferenceNotFound() throws Exception {
        when(imagingStudyReferenceRepository.findByStudyInstanceUid(STUDY_INSTANCE_UID)).thenReturn(null);

        imagingStudyService.updateImagingStudyAsAvailable(STUDY_INSTANCE_UID);
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenImagingStudyUuidIsNull() throws Exception {
        ImagingStudyReference reference = new ImagingStudyReference();
        reference.setImagingStudyUuid(null);
        when(imagingStudyReferenceRepository.findByStudyInstanceUid(STUDY_INSTANCE_UID)).thenReturn(reference);

        imagingStudyService.updateImagingStudyAsAvailable(STUDY_INSTANCE_UID);
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenImagingStudyUuidIsEmpty() throws Exception {
        ImagingStudyReference reference = new ImagingStudyReference();
        reference.setImagingStudyUuid("");
        when(imagingStudyReferenceRepository.findByStudyInstanceUid(STUDY_INSTANCE_UID)).thenReturn(reference);

        imagingStudyService.updateImagingStudyAsAvailable(STUDY_INSTANCE_UID);
    }

    @Test
    public void shouldUpdateImagingStudyAsAvailable() throws Exception {
        String imagingStudyUuid = "imaging-study-uuid-123";
        ImagingStudyReference reference = new ImagingStudyReference();
        reference.setImagingStudyUuid(imagingStudyUuid);
        
        when(imagingStudyReferenceRepository.findByStudyInstanceUid(STUDY_INSTANCE_UID)).thenReturn(reference);

        imagingStudyService.updateImagingStudyAsAvailable(STUDY_INSTANCE_UID);

        verify(imagingStudyReferenceRepository).findByStudyInstanceUid(STUDY_INSTANCE_UID);
        verify(openMRSService).updateFhirImagingStudyStatus(eq(imagingStudyUuid), anyList());
    }

    @Test(expected = IOException.class)
    public void shouldPropagateIOExceptionWhenUpdateFhirImagingStudyStatusFails() throws Exception {
        String imagingStudyUuid = "imaging-study-uuid-123";
        ImagingStudyReference reference = new ImagingStudyReference();
        reference.setImagingStudyUuid(imagingStudyUuid);
        
        when(imagingStudyReferenceRepository.findByStudyInstanceUid(STUDY_INSTANCE_UID)).thenReturn(reference);
        doThrow(new IOException("Network error")).when(openMRSService)
                .updateFhirImagingStudyStatus(eq(imagingStudyUuid), anyList());

        imagingStudyService.updateImagingStudyAsAvailable(STUDY_INSTANCE_UID);
    }
}
