package org.bahmni.module.pacsintegration.dto;

import org.junit.Test;

import static org.junit.Assert.*;

public class DicomMetadataDTOTest {

    @Test
    public void shouldInitializeMetadataMapInConstructor() {
        DicomMetadataDTO dto = new DicomMetadataDTO();

        assertNotNull(dto.getMetadata());
        assertTrue(dto.getMetadata().isEmpty());
    }

    @Test
    public void shouldAddDicomTagToMetadata() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setValue(new Object[]{"20240115"});

        dto.setDicomTag("00080022", field);

        assertEquals(1, dto.getMetadata().size());
        assertTrue(dto.getMetadata().containsKey("00080022"));
        assertEquals(field, dto.getMetadata().get("00080022"));
    }

    @Test
    public void shouldAddMultipleDicomTagsToMetadata() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field1 = new DicomMetadataDTO.DicomField();
        field1.setValue(new Object[]{"20240115"});
        DicomMetadataDTO.DicomField field2 = new DicomMetadataDTO.DicomField();
        field2.setValue(new Object[]{"143045"});

        dto.setDicomTag("00080022", field1);
        dto.setDicomTag("00080032", field2);

        assertEquals(2, dto.getMetadata().size());
        assertTrue(dto.getMetadata().containsKey("00080022"));
        assertTrue(dto.getMetadata().containsKey("00080032"));
    }

    @Test
    public void shouldOverwriteExistingDicomTag() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field1 = new DicomMetadataDTO.DicomField();
        field1.setValue(new Object[]{"20240115"});
        DicomMetadataDTO.DicomField field2 = new DicomMetadataDTO.DicomField();
        field2.setValue(new Object[]{"20240116"});

        dto.setDicomTag("00080022", field1);
        dto.setDicomTag("00080022", field2);

        assertEquals(1, dto.getMetadata().size());
        assertEquals(field2, dto.getMetadata().get("00080022"));
    }

    @Test
    public void shouldReturnAcquisitionDateWhenTagExists() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setValue(new Object[]{"20240115"});
        dto.setDicomTag(DicomMetadataDTO.AQUISITION_DATE_TAG, field);

        String result = dto.getAcquisitionDate();

        assertEquals("20240115", result);
    }

    @Test
    public void shouldReturnNullWhenAcquisitionDateTagDoesNotExist() {
        DicomMetadataDTO dto = new DicomMetadataDTO();

        String result = dto.getAcquisitionDate();

        assertNull(result);
    }

    @Test
    public void shouldReturnNullWhenAcquisitionDateValueIsNull() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setValue(null);
        dto.setDicomTag(DicomMetadataDTO.AQUISITION_DATE_TAG, field);

        String result = dto.getAcquisitionDate();

        assertNull(result);
    }

    @Test
    public void shouldReturnNullWhenAcquisitionDateValueIsEmpty() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setValue(new Object[]{});
        dto.setDicomTag(DicomMetadataDTO.AQUISITION_DATE_TAG, field);

        String result = dto.getAcquisitionDate();

        assertNull(result);
    }

    @Test
    public void shouldReturnFirstValueWhenAcquisitionDateHasMultipleValues() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setValue(new Object[]{"20240115", "20240116", "20240117"});
        dto.setDicomTag(DicomMetadataDTO.AQUISITION_DATE_TAG, field);

        String result = dto.getAcquisitionDate();

        assertEquals("20240115", result);
    }

    @Test
    public void shouldReturnAcquisitionTimeWhenTagExists() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setValue(new Object[]{"143045"});
        dto.setDicomTag(DicomMetadataDTO.AQUISITION_TIME_TAG, field);

        String result = dto.getAcquisitionTime();

        assertEquals("143045", result);
    }

    @Test
    public void shouldReturnNullWhenAcquisitionTimeTagDoesNotExist() {
        DicomMetadataDTO dto = new DicomMetadataDTO();

        String result = dto.getAcquisitionTime();

        assertNull(result);
    }

    @Test
    public void shouldReturnNullWhenAcquisitionTimeValueIsNull() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setValue(null);
        dto.setDicomTag(DicomMetadataDTO.AQUISITION_TIME_TAG, field);

        String result = dto.getAcquisitionTime();

        assertNull(result);
    }

    @Test
    public void shouldReturnNullWhenAcquisitionTimeValueIsEmpty() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setValue(new Object[]{});
        dto.setDicomTag(DicomMetadataDTO.AQUISITION_TIME_TAG, field);

        String result = dto.getAcquisitionTime();

        assertNull(result);
    }

    @Test
    public void shouldReturnAcquisitionDateTimeWhenTagExists() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setValue(new Object[]{"20240115143045+0530"});
        dto.setDicomTag(DicomMetadataDTO.AQUISITION_DATETIME_WITH_OFFSET_TAG, field);

        String result = dto.getAcquisitionDateTime();

        assertEquals("20240115143045+0530", result);
    }

    @Test
    public void shouldReturnNullWhenAcquisitionDateTimeTagDoesNotExist() {
        DicomMetadataDTO dto = new DicomMetadataDTO();

        String result = dto.getAcquisitionDateTime();

        assertNull(result);
    }

    @Test
    public void shouldReturnNullWhenAcquisitionDateTimeValueIsNull() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setValue(null);
        dto.setDicomTag(DicomMetadataDTO.AQUISITION_DATETIME_WITH_OFFSET_TAG, field);

        String result = dto.getAcquisitionDateTime();

        assertNull(result);
    }

    @Test
    public void shouldReturnNullWhenAcquisitionDateTimeValueIsEmpty() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setValue(new Object[]{});
        dto.setDicomTag(DicomMetadataDTO.AQUISITION_DATETIME_WITH_OFFSET_TAG, field);

        String result = dto.getAcquisitionDateTime();

        assertNull(result);
    }

    @Test
    public void shouldReturnTimezoneOffsetFromUTCWhenTagExists() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setValue(new Object[]{"+0530"});
        dto.setDicomTag(DicomMetadataDTO.TIMEZONE_OFFSET_FROM_UTC_TAG, field);

        String result = dto.getTimezoneOffsetFromUTC();

        assertEquals("+0530", result);
    }

    @Test
    public void shouldReturnNullWhenTimezoneOffsetFromUTCTagDoesNotExist() {
        DicomMetadataDTO dto = new DicomMetadataDTO();

        String result = dto.getTimezoneOffsetFromUTC();

        assertNull(result);
    }

    @Test
    public void shouldReturnNullWhenTimezoneOffsetFromUTCValueIsNull() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setValue(null);
        dto.setDicomTag(DicomMetadataDTO.TIMEZONE_OFFSET_FROM_UTC_TAG, field);

        String result = dto.getTimezoneOffsetFromUTC();

        assertNull(result);
    }

    @Test
    public void shouldReturnNullWhenTimezoneOffsetFromUTCValueIsEmpty() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setValue(new Object[]{});
        dto.setDicomTag(DicomMetadataDTO.TIMEZONE_OFFSET_FROM_UTC_TAG, field);

        String result = dto.getTimezoneOffsetFromUTC();

        assertNull(result);
    }

    @Test
    public void shouldReturnNullWhenMetadataIsNull() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        dto.setMetadata(null);

        assertNull(dto.getAcquisitionDate());
        assertNull(dto.getAcquisitionTime());
        assertNull(dto.getAcquisitionDateTime());
        assertNull(dto.getTimezoneOffsetFromUTC());
    }

    @Test
    public void shouldConvertValueToStringUsingToString() {
        DicomMetadataDTO dto = new DicomMetadataDTO();
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setValue(new Object[]{12345});
        dto.setDicomTag(DicomMetadataDTO.AQUISITION_DATE_TAG, field);

        String result = dto.getAcquisitionDate();

        assertEquals("12345", result);
    }

    // Tests for DicomField inner class
    @Test
    public void shouldSetAndGetVrInDicomField() {
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setVr("DA");

        assertEquals("DA", field.getVr());
    }

    @Test
    public void shouldSetAndGetValueInDicomField() {
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        Object[] values = new Object[]{"20240115"};
        field.setValue(values);

        assertArrayEquals(values, field.getValue());
    }

    @Test
    public void shouldInitializeDicomFieldWithDefaultConstructor() {
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();

        assertNull(field.getVr());
        assertNull(field.getValue());
    }

    @Test
    public void shouldHandleComplexDicomFieldWithMultipleProperties() {
        DicomMetadataDTO.DicomField field = new DicomMetadataDTO.DicomField();
        field.setVr("DT");
        field.setValue(new Object[]{"20240115143045.123456+0530"});

        assertEquals("DT", field.getVr());
        assertEquals(1, field.getValue().length);
        assertEquals("20240115143045.123456+0530", field.getValue()[0]);
    }

    @Test
    public void shouldVerifyDicomTagConstants() {
        assertEquals("00080022", DicomMetadataDTO.AQUISITION_DATE_TAG);
        assertEquals("00080032", DicomMetadataDTO.AQUISITION_TIME_TAG);
        assertEquals("0008002A", DicomMetadataDTO.AQUISITION_DATETIME_WITH_OFFSET_TAG);
        assertEquals("00080201", DicomMetadataDTO.TIMEZONE_OFFSET_FROM_UTC_TAG);
    }
}
