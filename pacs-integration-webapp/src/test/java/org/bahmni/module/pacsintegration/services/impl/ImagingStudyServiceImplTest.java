package org.bahmni.module.pacsintegration.services.impl;

import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.FhirImagingStudy;
import org.bahmni.module.pacsintegration.atomfeed.mappers.ImagingStudyMapper;
import org.bahmni.module.pacsintegration.model.ImagingStudyReference;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.repository.ImagingStudyReferenceRepository;
import org.bahmni.module.pacsintegration.repository.OrderRepository;
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
public class ImagingStudyServiceImplTest {

    @Mock
    private OpenMRSService openMRSService;

    @Mock
    private ImagingStudyMapper imagingStudyMapper;

    @Mock
    private ImagingStudyReferenceRepository imagingStudyReferenceRepository;

    @Mock
    private OrderRepository orderRepository;

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
        Order order = new Order();
        order.setId(1);
        
        when(imagingStudyMapper.buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION))
                .thenReturn(fhirImagingStudy);
        when(openMRSService.createFhirImagingStudy(fhirImagingStudy)).thenReturn(expectedUuid);
        when(orderRepository.findByOrderUuid(ORDER_UUID)).thenReturn(order);

        String result = imagingStudyService.createImagingStudy(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);

        assertNotNull(result);
        assertEquals(expectedUuid, result);
        verify(imagingStudyMapper).buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);
        verify(openMRSService).createFhirImagingStudy(fhirImagingStudy);
        verify(orderRepository).findByOrderUuid(ORDER_UUID);
        verify(imagingStudyReferenceRepository).save(any(ImagingStudyReference.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenStudyInstanceUIDIsNull() throws Exception {
        imagingStudyService.createImagingStudy(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, null, DESCRIPTION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenStudyInstanceUIDIsEmpty() throws Exception {
        imagingStudyService.createImagingStudy(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, "", DESCRIPTION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenStudyInstanceUIDIsWhitespace() throws Exception {
        imagingStudyService.createImagingStudy(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, "   ", DESCRIPTION);
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenOpenMRSServiceFails() throws Exception {
        FhirImagingStudy fhirImagingStudy = new FhirImagingStudy();
        when(imagingStudyMapper.buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION))
                .thenReturn(fhirImagingStudy);
        when(openMRSService.createFhirImagingStudy(fhirImagingStudy))
                .thenThrow(new IOException("Failed to create imaging study"));

        imagingStudyService.createImagingStudy(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenOpenMRSReturnsNullUuid() throws Exception {
        FhirImagingStudy fhirImagingStudy = new FhirImagingStudy();
        when(imagingStudyMapper.buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION))
                .thenReturn(fhirImagingStudy);
        when(openMRSService.createFhirImagingStudy(fhirImagingStudy)).thenReturn(null);

        imagingStudyService.createImagingStudy(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenOpenMRSReturnsEmptyUuid() throws Exception {
        FhirImagingStudy fhirImagingStudy = new FhirImagingStudy();
        when(imagingStudyMapper.buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION))
                .thenReturn(fhirImagingStudy);
        when(openMRSService.createFhirImagingStudy(fhirImagingStudy)).thenReturn("");

        imagingStudyService.createImagingStudy(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);
    }

    @Test
    public void shouldCreateImagingStudyAndSaveReference() throws Exception {
        String expectedUuid = "imaging-study-uuid-123";
        FhirImagingStudy fhirImagingStudy = new FhirImagingStudy();
        Order order = new Order();
        order.setId(1);
        
        when(imagingStudyMapper.buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION))
                .thenReturn(fhirImagingStudy);
        when(openMRSService.createFhirImagingStudy(fhirImagingStudy)).thenReturn(expectedUuid);
        when(orderRepository.findByOrderUuid(ORDER_UUID)).thenReturn(order);

        String result = imagingStudyService.createImagingStudy(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);

        assertNotNull(result);
        assertEquals(expectedUuid, result);
        verify(imagingStudyMapper).buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);
        verify(openMRSService).createFhirImagingStudy(fhirImagingStudy);
        verify(orderRepository).findByOrderUuid(ORDER_UUID);
        verify(imagingStudyReferenceRepository).save(any(ImagingStudyReference.class));
    }

    @Test
    public void shouldCreateImagingStudyButNotSaveReferenceWhenOrderNotFound() throws Exception {
        String expectedUuid = "imaging-study-uuid-123";
        FhirImagingStudy fhirImagingStudy = new FhirImagingStudy();
        
        when(imagingStudyMapper.buildFhirPayload(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION))
                .thenReturn(fhirImagingStudy);
        when(openMRSService.createFhirImagingStudy(fhirImagingStudy)).thenReturn(expectedUuid);
        when(orderRepository.findByOrderUuid(ORDER_UUID)).thenReturn(null);

        String result = imagingStudyService.createImagingStudy(ORDER_UUID, PATIENT_UUID, LOCATION_UUID, STUDY_INSTANCE_UID, DESCRIPTION);

        assertNotNull(result);
        assertEquals(expectedUuid, result);
        verify(orderRepository).findByOrderUuid(ORDER_UUID);
        verify(imagingStudyReferenceRepository, never()).save(any(ImagingStudyReference.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenUpdateStatusWithNullStudyInstanceUID() throws Exception {
        imagingStudyService.updateImagingStudyStatus(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenUpdateStatusWithEmptyStudyInstanceUID() throws Exception {
        imagingStudyService.updateImagingStudyStatus("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenUpdateStatusWithBlankStudyInstanceUID() throws Exception {
        imagingStudyService.updateImagingStudyStatus("   ");
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenImagingStudyReferenceNotFound() throws Exception {
        when(imagingStudyReferenceRepository.findByStudyInstanceUid(STUDY_INSTANCE_UID)).thenReturn(null);

        imagingStudyService.updateImagingStudyStatus(STUDY_INSTANCE_UID);
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenImagingStudyUuidIsNull() throws Exception {
        ImagingStudyReference reference = new ImagingStudyReference();
        reference.setImagingStudyUuid(null);
        when(imagingStudyReferenceRepository.findByStudyInstanceUid(STUDY_INSTANCE_UID)).thenReturn(reference);

        imagingStudyService.updateImagingStudyStatus(STUDY_INSTANCE_UID);
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionWhenImagingStudyUuidIsEmpty() throws Exception {
        ImagingStudyReference reference = new ImagingStudyReference();
        reference.setImagingStudyUuid("");
        when(imagingStudyReferenceRepository.findByStudyInstanceUid(STUDY_INSTANCE_UID)).thenReturn(reference);

        imagingStudyService.updateImagingStudyStatus(STUDY_INSTANCE_UID);
    }

    @Test
    public void shouldUpdateImagingStudyStatus() throws Exception {
        String imagingStudyUuid = "imaging-study-uuid-123";
        ImagingStudyReference reference = new ImagingStudyReference();
        reference.setImagingStudyUuid(imagingStudyUuid);
        
        when(imagingStudyReferenceRepository.findByStudyInstanceUid(STUDY_INSTANCE_UID)).thenReturn(reference);

        imagingStudyService.updateImagingStudyStatus(STUDY_INSTANCE_UID);

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

        imagingStudyService.updateImagingStudyStatus(STUDY_INSTANCE_UID);
    }
}
