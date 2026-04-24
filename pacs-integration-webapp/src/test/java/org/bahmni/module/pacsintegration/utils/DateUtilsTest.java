package org.bahmni.module.pacsintegration.utils;

import org.junit.Test;
import java.util.Calendar;
import java.util.Date;
import static org.junit.Assert.*;

public class DateUtilsTest {

    @Test
    public void shouldFormatDateToFhirDateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.JANUARY, 15, 14, 30, 45);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();

        String result = DateUtils.formatFhirDateTime(date);

        assertNotNull(result);
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}"));
        assertTrue(result.contains("2024-01-15T"));
    }

    @Test
    public void shouldFormatEpochDateToFhirDateTime() {
        Date epochDate = new Date(0);

        String result = DateUtils.formatFhirDateTime(epochDate);

        assertNotNull(result);
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    public void shouldCombineDicomDateAndTime() {
        String dicomDate = "20240115";
        String dicomTime = "143045";

        Date result = DateUtils.combineDicomDateTime(dicomDate, dicomTime);

        assertNotNull(result);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(result);
        assertEquals(2024, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
        assertEquals(15, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(14, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, calendar.get(Calendar.MINUTE));
        assertEquals(45, calendar.get(Calendar.SECOND));
    }

    @Test
    public void shouldCombineDicomDateAndTimeWithFractionalSeconds() {
        String dicomDate = "20240115";
        String dicomTime = "143045.123456";

        Date result = DateUtils.combineDicomDateTime(dicomDate, dicomTime);

        assertNotNull(result);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(result);
        assertEquals(2024, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
        assertEquals(15, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(14, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, calendar.get(Calendar.MINUTE));
        assertEquals(45, calendar.get(Calendar.SECOND));
    }

    @Test
    public void shouldReturnNullWhenDicomDateTimeIsInvalidFormat() {
        String dicomDate = "invalid";
        String dicomTime = "143045";

        Date result = DateUtils.combineDicomDateTime(dicomDate, dicomTime);

        assertNull(result);
    }

    @Test
    public void shouldParseDicomDateTimeWithPositiveOffset() {
        String dicomDateTime = "20240115143045+0530";

        Date result = DateUtils.parseDicomDateTimeWithOffset(dicomDateTime);

        assertNotNull(result);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        calendar.setTime(result);
        
        assertEquals(2024, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
        assertEquals(15, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(9, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, calendar.get(Calendar.MINUTE));
        assertEquals(45, calendar.get(Calendar.SECOND));
    }

    @Test
    public void shouldParseDicomDateTimeWithNegativeOffset() {
        String dicomDateTime = "20240115143045-0500";

        Date result = DateUtils.parseDicomDateTimeWithOffset(dicomDateTime);

        assertNotNull(result);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        calendar.setTime(result);
        
        assertEquals(2024, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
        assertEquals(15, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(19, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, calendar.get(Calendar.MINUTE));
        assertEquals(45, calendar.get(Calendar.SECOND));
    }

    @Test
    public void shouldParseDicomDateTimeWithZeroOffset() {
        String dicomDateTime = "20240115143045+0000";

        Date result = DateUtils.parseDicomDateTimeWithOffset(dicomDateTime);

        assertNotNull(result);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        calendar.setTime(result);

        assertEquals(2024, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
        assertEquals(15, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(14, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, calendar.get(Calendar.MINUTE));
        assertEquals(45, calendar.get(Calendar.SECOND));
    }

    @Test
    public void shouldParseDicomDateTimeWithOffsetAndFractionalSeconds() {
        String dicomDateTime = "20240115143045.123456+0530";

        Date result = DateUtils.parseDicomDateTimeWithOffset(dicomDateTime);

        assertNotNull(result);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        calendar.setTime(result);

        assertEquals(2024, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
        assertEquals(15, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(9, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, calendar.get(Calendar.MINUTE));
        assertEquals(45, calendar.get(Calendar.SECOND));
    }

    @Test
    public void shouldReturnNullWhenDicomDateTimeWithOffsetIsInvalidFormat() {
        String dicomDateTime = "invalid-datetime";

        Date result = DateUtils.parseDicomDateTimeWithOffset(dicomDateTime);

        assertNull(result);
    }
}
