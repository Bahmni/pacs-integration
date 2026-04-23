package org.bahmni.module.pacsintegration.services.impl;

import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.FhirImagingStudy;
import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.JsonPatchOperation;
import org.bahmni.module.pacsintegration.atomfeed.mappers.ImagingStudyMapper;
import org.bahmni.module.pacsintegration.dto.DicomMetadataDTO;
import org.bahmni.module.pacsintegration.model.ImagingStudyReference;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.repository.ImagingStudyReferenceRepository;
import org.bahmni.module.pacsintegration.services.Dcm4CheeService;
import org.bahmni.module.pacsintegration.services.OpenMRSService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImagingStudyServiceImplTest  {

    @Mock
    private OpenMRSService openMRSService;

    @Mock
    private Dcm4CheeService dcm4CheeService;

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

    @Test
    public void shouldFetchMetadataFromDcm4CheeService() throws Exception {
        String imagingStudyUuid = "imaging-study-uuid-123";
        ImagingStudyReference reference = new ImagingStudyReference();
        reference.setImagingStudyUuid(imagingStudyUuid);
        
        DicomMetadataDTO metadata = createMetadataWithAcquisitionDateTime("20240115143000+0530");
        DicomMetadataDTO[] metadataArray = new DicomMetadataDTO[]{metadata};
        
        when(imagingStudyReferenceRepository.findByStudyInstanceUid(STUDY_INSTANCE_UID)).thenReturn(reference);
        when(dcm4CheeService.fetchStudyMetadata(STUDY_INSTANCE_UID)).thenReturn(metadataArray);

        imagingStudyService.updateImagingStudyAsAvailable(STUDY_INSTANCE_UID);

        verify(dcm4CheeService).fetchStudyMetadata(STUDY_INSTANCE_UID);
    }

    @Test
    public void shouldUpdateWithDateExtensionWhenAcquisitionDateTimePresent() throws Exception {
        String imagingStudyUuid = "imaging-study-uuid-123";
        ImagingStudyReference reference = new ImagingStudyReference();
        reference.setImagingStudyUuid(imagingStudyUuid);
        
        DicomMetadataDTO metadata = createMetadataWithAcquisitionDateTime("20240115143000+0530");
        DicomMetadataDTO[] metadataArray = new DicomMetadataDTO[]{metadata};
        
        when(imagingStudyReferenceRepository.findByStudyInstanceUid(STUDY_INSTANCE_UID)).thenReturn(reference);
        when(dcm4CheeService.fetchStudyMetadata(STUDY_INSTANCE_UID)).thenReturn(metadataArray);

        imagingStudyService.updateImagingStudyAsAvailable(STUDY_INSTANCE_UID);

        ArgumentCaptor<List> patchOpsCaptor = ArgumentCaptor.forClass(List.class);
        verify(openMRSService).updateFhirImagingStudyStatus(eq(imagingStudyUuid), patchOpsCaptor.capture());
        
        List<JsonPatchOperation> patchOperations = patchOpsCaptor.getValue();
        assertEquals(2, patchOperations.size());
        
        JsonPatchOperation statusOp = patchOperations.get(0);
        assertEquals("replace", statusOp.getOp());
        assertEquals("/status", statusOp.getPath());
        assertEquals("available", statusOp.getValue());
        
        JsonPatchOperation extensionOp = patchOperations.get(1);
        assertEquals("add", extensionOp.getOp());
        assertEquals("/extension", extensionOp.getPath());
        assertNotNull(extensionOp.getValue());
    }

    @Test
    public void shouldUpdateWithDateExtensionWhenDateTimeAndOffsetPresent() throws Exception {
        String imagingStudyUuid = "imaging-study-uuid-123";
        ImagingStudyReference reference = new ImagingStudyReference();
        reference.setImagingStudyUuid(imagingStudyUuid);
        
        DicomMetadataDTO metadata = createMetadataWithDateTimeAndOffset("20240115", "143000", "+0530");
        DicomMetadataDTO[] metadataArray = new DicomMetadataDTO[]{metadata};
        
        when(imagingStudyReferenceRepository.findByStudyInstanceUid(STUDY_INSTANCE_UID)).thenReturn(reference);
        when(dcm4CheeService.fetchStudyMetadata(STUDY_INSTANCE_UID)).thenReturn(metadataArray);

        imagingStudyService.updateImagingStudyAsAvailable(STUDY_INSTANCE_UID);

        ArgumentCaptor<List> patchOpsCaptor = ArgumentCaptor.forClass(List.class);
        verify(openMRSService).updateFhirImagingStudyStatus(eq(imagingStudyUuid), patchOpsCaptor.capture());
        
        List<JsonPatchOperation> patchOperations = patchOpsCaptor.getValue();
        assertEquals(2, patchOperations.size());
        
        JsonPatchOperation extensionOp = patchOperations.get(1);
        assertEquals("add", extensionOp.getOp());
        assertEquals("/extension", extensionOp.getPath());
        
        List<Map<String, Object>> extensionList = (List<Map<String, Object>>) extensionOp.getValue();
        assertNotNull(extensionList);
        assertEquals(1, extensionList.size());
        
        Map<String, Object> extension = extensionList.get(0);
        assertEquals("http://fhir.bahmni.org/ext/imaging-study/completion-date", extension.get("url"));
        assertNotNull(extension.get("valueDateTime"));
    }

    @Test
    public void shouldUpdateWithDateExtensionWhenDateAndTimePresent() throws Exception {
        String imagingStudyUuid = "imaging-study-uuid-123";
        ImagingStudyReference reference = new ImagingStudyReference();
        reference.setImagingStudyUuid(imagingStudyUuid);
        
        DicomMetadataDTO metadata = createMetadataWithDateAndTime("20240115", "143000");
        DicomMetadataDTO[] metadataArray = new DicomMetadataDTO[]{metadata};
        
        when(imagingStudyReferenceRepository.findByStudyInstanceUid(STUDY_INSTANCE_UID)).thenReturn(reference);
        when(dcm4CheeService.fetchStudyMetadata(STUDY_INSTANCE_UID)).thenReturn(metadataArray);

        imagingStudyService.updateImagingStudyAsAvailable(STUDY_INSTANCE_UID);

        ArgumentCaptor<List> patchOpsCaptor = ArgumentCaptor.forClass(List.class);
        verify(openMRSService).updateFhirImagingStudyStatus(eq(imagingStudyUuid), patchOpsCaptor.capture());
        
        List<JsonPatchOperation> patchOperations = patchOpsCaptor.getValue();
        assertEquals(2, patchOperations.size());
        
        JsonPatchOperation extensionOp = patchOperations.get(1);
        assertEquals("add", extensionOp.getOp());
        assertEquals("/extension", extensionOp.getPath());
        assertNotNull(extensionOp.getValue());
    }

    @Test
    public void shouldUpdateWithoutDateExtensionWhenMetadataIsNull() throws Exception {
        String imagingStudyUuid = "imaging-study-uuid-123";
        ImagingStudyReference reference = new ImagingStudyReference();
        reference.setImagingStudyUuid(imagingStudyUuid);
        
        when(imagingStudyReferenceRepository.findByStudyInstanceUid(STUDY_INSTANCE_UID)).thenReturn(reference);
        when(dcm4CheeService.fetchStudyMetadata(STUDY_INSTANCE_UID)).thenReturn(null);

        imagingStudyService.updateImagingStudyAsAvailable(STUDY_INSTANCE_UID);

        ArgumentCaptor<List> patchOpsCaptor = ArgumentCaptor.forClass(List.class);
        verify(openMRSService).updateFhirImagingStudyStatus(eq(imagingStudyUuid), patchOpsCaptor.capture());
        
        List<JsonPatchOperation> patchOperations = patchOpsCaptor.getValue();
        assertEquals(1, patchOperations.size());
        
        JsonPatchOperation statusOp = patchOperations.get(0);
        assertEquals("replace", statusOp.getOp());
        assertEquals("/status", statusOp.getPath());
        assertEquals("available", statusOp.getValue());
    }

    @Test
    public void shouldUpdateWithoutDateExtensionWhenMetadataIsEmpty() throws Exception {
        String imagingStudyUuid = "imaging-study-uuid-123";
        ImagingStudyReference reference = new ImagingStudyReference();
        reference.setImagingStudyUuid(imagingStudyUuid);
        
        DicomMetadataDTO[] metadataArray = new DicomMetadataDTO[0];
        
        when(imagingStudyReferenceRepository.findByStudyInstanceUid(STUDY_INSTANCE_UID)).thenReturn(reference);
        when(dcm4CheeService.fetchStudyMetadata(STUDY_INSTANCE_UID)).thenReturn(metadataArray);

        imagingStudyService.updateImagingStudyAsAvailable(STUDY_INSTANCE_UID);

        ArgumentCaptor<List> patchOpsCaptor = ArgumentCaptor.forClass(List.class);
        verify(openMRSService).updateFhirImagingStudyStatus(eq(imagingStudyUuid), patchOpsCaptor.capture());
        
        List<JsonPatchOperation> patchOperations = patchOpsCaptor.getValue();
        assertEquals(1, patchOperations.size());
        
        JsonPatchOperation statusOp = patchOperations.get(0);
        assertEquals("replace", statusOp.getOp());
        assertEquals("/status", statusOp.getPath());
        assertEquals("available", statusOp.getValue());
    }

    @Test
    public void shouldVerifyPatchOperationsContainCorrectDateExtension() throws Exception {
        String imagingStudyUuid = "imaging-study-uuid-123";
        ImagingStudyReference reference = new ImagingStudyReference();
        reference.setImagingStudyUuid(imagingStudyUuid);
        
        DicomMetadataDTO metadata = createMetadataWithAcquisitionDateTime("20240115143000+0530");
        DicomMetadataDTO[] metadataArray = new DicomMetadataDTO[]{metadata};
        
        when(imagingStudyReferenceRepository.findByStudyInstanceUid(STUDY_INSTANCE_UID)).thenReturn(reference);
        when(dcm4CheeService.fetchStudyMetadata(STUDY_INSTANCE_UID)).thenReturn(metadataArray);

        imagingStudyService.updateImagingStudyAsAvailable(STUDY_INSTANCE_UID);

        ArgumentCaptor<List> patchOpsCaptor = ArgumentCaptor.forClass(List.class);
        verify(openMRSService).updateFhirImagingStudyStatus(eq(imagingStudyUuid), patchOpsCaptor.capture());
        
        List<JsonPatchOperation> patchOperations = patchOpsCaptor.getValue();
        assertEquals(2, patchOperations.size());
        
        JsonPatchOperation statusOp = patchOperations.get(0);
        assertEquals("replace", statusOp.getOp());
        assertEquals("/status", statusOp.getPath());
        assertEquals("available", statusOp.getValue());
        
        JsonPatchOperation extensionOp = patchOperations.get(1);
        assertEquals("add", extensionOp.getOp());
        assertEquals("/extension", extensionOp.getPath());
        
        List<Map<String, Object>> extensionList = (List<Map<String, Object>>) extensionOp.getValue();
        assertNotNull(extensionList);
        assertEquals(1, extensionList.size());
        
        Map<String, Object> extension = extensionList.get(0);
        assertEquals("http://fhir.bahmni.org/ext/imaging-study/completion-date", extension.get("url"));
        assertNotNull(extension.get("valueDateTime"));
        String valueDateTime = (String) extension.get("valueDateTime");
        assertEquals("2024-01-15T14:30:00", valueDateTime);
    }

    // Helper methods to create metadata DTOs
    private DicomMetadataDTO createMetadataWithAcquisitionDateTime(String acquisitionDateTime) {
        DicomMetadataDTO metadata = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setValue(new Object[]{acquisitionDateTime});
        metadata.setDicomTag(DicomMetadataDTO.AQUISITION_DATETIME_WITH_OFFSET_TAG, field);
        return metadata;
    }

    private DicomMetadataDTO createMetadataWithDateTimeAndOffset(String date, String time, String offset) {
        DicomMetadataDTO metadata = new DicomMetadataDTO();
        
        DicomMetadataDTO.DicomField dateField = new DicomMetadataDTO.DicomField();
        dateField.setValue(new Object[]{date});
        metadata.setDicomTag(DicomMetadataDTO.AQUISITION_DATE_TAG, dateField);
        
        DicomMetadataDTO.DicomField timeField = new DicomMetadataDTO.DicomField();
        timeField.setValue(new Object[]{time});
        metadata.setDicomTag(DicomMetadataDTO.AQUISITION_TIME_TAG, timeField);
        
        DicomMetadataDTO.DicomField offsetField = new DicomMetadataDTO.DicomField();
        offsetField.setValue(new Object[]{offset});
        metadata.setDicomTag(DicomMetadataDTO.TIMEZONE_OFFSET_FROM_UTC_TAG, offsetField);
        
        return metadata;
    }

    private DicomMetadataDTO createMetadataWithDateAndTime(String date, String time) {
        DicomMetadataDTO metadata = new DicomMetadataDTO();
        
        DicomMetadataDTO.DicomField dateField = new DicomMetadataDTO.DicomField();
        dateField.setValue(new Object[]{date});
        metadata.setDicomTag(DicomMetadataDTO.AQUISITION_DATE_TAG, dateField);
        
        DicomMetadataDTO.DicomField timeField = new DicomMetadataDTO.DicomField();
        timeField.setValue(new Object[]{time});
        metadata.setDicomTag(DicomMetadataDTO.AQUISITION_TIME_TAG, timeField);
        
        return metadata;
    }
}
