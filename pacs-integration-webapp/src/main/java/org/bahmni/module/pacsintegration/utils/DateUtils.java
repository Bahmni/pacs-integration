package org.bahmni.module.pacsintegration.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class DateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);
    
    private static final String FHIR_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DICOM_DATETIME_FORMAT = "yyyyMMddHHmmss";
    private static final String DICOM_DATETIME_FORMAT_WITH_OFFSET = "yyyyMMddHHmmssXX";

    private DateUtils() {}


    public static String formatFhirDateTime(Date date) {
        SimpleDateFormat fhirFormat = new SimpleDateFormat(FHIR_DATETIME_FORMAT);
        return fhirFormat.format(date);
    }


    public static Date combineDicomDateTime(String dicomDate, String dicomTime) {
        try {
            // Remove fractional seconds if present (e.g., 141836.123456 -> 141836)
            String timePart = dicomTime;
            if (timePart.contains(".")) {
                timePart = timePart.substring(0, timePart.indexOf("."));
            }

            String dateTimeStr = dicomDate + timePart;
            SimpleDateFormat dicomFormat = new SimpleDateFormat(DICOM_DATETIME_FORMAT);
            dicomFormat.setLenient(false);
            return dicomFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            logger.error("Failed to parse DICOM date/time: date={}, time={}", dicomDate, dicomTime, e);
        }
        return null;
    }

    public static Date parseDicomDateTimeWithOffset(String dicomDateTime) {
        try {
            String cleaned = dicomDateTime.trim();
            
            // Remove fractional seconds if present (SimpleDateFormat has limits on fractional precision)
            cleaned = cleaned.replaceFirst("\\.\\d+", "");

            SimpleDateFormat formatWithOffset = new SimpleDateFormat(DICOM_DATETIME_FORMAT_WITH_OFFSET);
            formatWithOffset.setTimeZone(TimeZone.getTimeZone("UTC"));
            formatWithOffset.setLenient(false);
            return formatWithOffset.parse(cleaned);
            
        } catch (ParseException e) {
            logger.error("Failed to parse with offset : {}", dicomDateTime, e);
        }
        return null;
    }
}
