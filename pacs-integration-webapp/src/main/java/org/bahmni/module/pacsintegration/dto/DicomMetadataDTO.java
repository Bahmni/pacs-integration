package org.bahmni.module.pacsintegration.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DicomMetadataDTO {

    public static final String AQUISITION_DATE_TAG = "00080022";
    public static final String AQUISITION_TIME_TAG = "00080032";
    private Map<String, DicomField> metadata = new HashMap<>();

    public DicomMetadataDTO() {
    }

    @JsonAnySetter
    public void setDicomTag(String tagName, DicomField tagValue) {
        this.metadata.put(tagName, tagValue);
    }

    public String getAcquisitionDate() {
        return getFirstValue(AQUISITION_DATE_TAG);
    }

    public String getAcquisitionTime() {
        return getFirstValue(AQUISITION_TIME_TAG);
    }

    private String getFirstValue(String tag) {
        if (metadata == null) {
            return null;
        }
        DicomField field = metadata.get(tag);
        if (field == null || field.getValue() == null || field.getValue().length == 0) {
            return null;
        }
        return field.getValue()[0].toString();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DicomField {
        @Getter
        @Setter
        private String vr;
        private Object[] Value;

        public DicomField() {}

        @JsonProperty("Value")
        public Object[] getValue() {
            return Value;
        }

        @JsonProperty("Value")
        public void setValue(Object[] value) {
            Value = value;
        }
    }
}
