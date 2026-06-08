package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7;

public class Constants {
    public static final String FIELD_SEPARATOR = "|";
    public static final String ENCODING_CHARACTERS= "^~\\&";
    public static final String HL7_MESSAGE_VERSION = "2.5";
    public static final String HL7_MESSAGE_CODE = "ORM";
    public static final String HL7_TRIGGER_EVENT = "O01";
    public static final String HL7_PROCESSING_PROD_MODE = "P";

    public static final String PATIENT_IDENTIFIER_TYPE_CODE = "MR";
    public static final String PATIENT_IDENTIFIER_ASSIGNING_AUTHORITY = "Bahmni EMR";

    public static final String NEW_ORDER = "NW";
    public static final String CANCEL_ORDER = "CA";
    public static final String HL7_SCHEDULED_STATUS_CODE = "SC";
    public static final String HL7_CANCELLED_STATUS_CODE = "CA";

    public static final String ORDER_ACTION_DISCONTINUE = "DISCONTINUE";
}
