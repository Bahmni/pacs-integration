package org.bahmni.module.pacsintegration.atomfeed.mappers;

import org.bahmni.module.pacsintegration.atomfeed.contract.fhir.FhirImagingStudy;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ImagingStudyMapperTest {

    private ImagingStudyMapper imagingStudyMapper;

    private static final String ORDER_UUID = "order-uuid-123";
    private static final String PATIENT_UUID = "patient-uuid-456";
    private static final String LOCATION_UUID = "location-uuid-789";
    private static final String STUDY_INSTANCE_UID = "1.2.826.0.1.3680043.8.498.12345";
    private static final String DESCRIPTION = "Chest X-Ray";

    @Before
    public void setUp() {
        imagingStudyMapper = new ImagingStudyMapper();
    }

    @Test
    public void shouldBuildFhirPayloadWithAllParameters() {
        FhirImagingStudy result = imagingStudyMapper.buildFhirPayload(
                ORDER_UUID,
                PATIENT_UUID,
                LOCATION_UUID,
                STUDY_INSTANCE_UID,
                DESCRIPTION
        );

        assertNotNull(result);
        assertEquals("ImagingStudy", result.getResourceType());
        assertEquals("unknown", result.getStatus());

        assertNotNull(result.getIdentifier());
        assertEquals(1, result.getIdentifier().size());
        assertEquals("urn:dicom:uid", result.getIdentifier().get(0).getSystem());
        assertEquals(STUDY_INSTANCE_UID, result.getIdentifier().get(0).getValue());

        assertNotNull(result.getSubject());
        assertEquals("Patient/" + PATIENT_UUID, result.getSubject().getReference());

        assertNotNull(result.getBasedOn());
        assertEquals(1, result.getBasedOn().size());
        assertEquals("ServiceRequest/" + ORDER_UUID, result.getBasedOn().get(0).getReference());

        assertNotNull(result.getLocation());
        assertEquals("Location/" + LOCATION_UUID, result.getLocation().getReference());

        assertEquals(DESCRIPTION, result.getDescription());
    }

    @Test
    public void shouldBuildFhirPayloadWithNullLocation() {
        FhirImagingStudy result = imagingStudyMapper.buildFhirPayload(
                ORDER_UUID,
                PATIENT_UUID,
                null,
                STUDY_INSTANCE_UID,
                DESCRIPTION
        );

        assertNotNull(result);
        assertEquals("ImagingStudy", result.getResourceType());
        assertEquals("unknown", result.getStatus());

        assertNotNull(result.getIdentifier());
        assertEquals(1, result.getIdentifier().size());
        assertEquals(STUDY_INSTANCE_UID, result.getIdentifier().get(0).getValue());
        assertNotNull(result.getSubject());
        assertNotNull(result.getBasedOn());
        assertEquals(DESCRIPTION, result.getDescription());

        assertNull(result.getLocation());
    }

    @Test
    public void shouldBuildFhirPayloadWithEmptyLocation() {
        FhirImagingStudy result = imagingStudyMapper.buildFhirPayload(
                ORDER_UUID,
                PATIENT_UUID,
                "",
                STUDY_INSTANCE_UID,
                DESCRIPTION
        );

        assertNotNull(result);

        assertNull(result.getLocation());

        assertNotNull(result.getIdentifier());
        assertNotNull(result.getSubject());
        assertNotNull(result.getBasedOn());
        assertEquals(DESCRIPTION, result.getDescription());
    }

    @Test
    public void shouldBuildFhirPayloadWithNullDescription() {
        FhirImagingStudy result = imagingStudyMapper.buildFhirPayload(
                ORDER_UUID,
                PATIENT_UUID,
                LOCATION_UUID,
                STUDY_INSTANCE_UID,
                null
        );

        assertNotNull(result);
        assertEquals("ImagingStudy", result.getResourceType());
        assertEquals("unknown", result.getStatus());

        assertNotNull(result.getIdentifier());
        assertEquals(1, result.getIdentifier().size());
        assertEquals(STUDY_INSTANCE_UID, result.getIdentifier().get(0).getValue());
        assertNotNull(result.getSubject());
        assertNotNull(result.getBasedOn());
        assertNotNull(result.getLocation());

        assertNull(result.getDescription());
    }

    @Test
    public void shouldBuildFhirPayloadWithEmptyDescription() {
        FhirImagingStudy result = imagingStudyMapper.buildFhirPayload(
                ORDER_UUID,
                PATIENT_UUID,
                LOCATION_UUID,
                STUDY_INSTANCE_UID,
                ""
        );

        assertNotNull(result);

        assertNull(result.getDescription());

        assertNotNull(result.getIdentifier());
        assertNotNull(result.getSubject());
        assertNotNull(result.getBasedOn());
        assertNotNull(result.getLocation());
    }

    @Test
    public void shouldBuildFhirPayloadWithNullLocationAndDescription() {
        FhirImagingStudy result = imagingStudyMapper.buildFhirPayload(
                ORDER_UUID,
                PATIENT_UUID,
                null,
                STUDY_INSTANCE_UID,
                null
        );

        assertNotNull(result);
        assertEquals("ImagingStudy", result.getResourceType());
        assertEquals("unknown", result.getStatus());

        assertNotNull(result.getIdentifier());
        assertEquals(1, result.getIdentifier().size());
        assertEquals("urn:dicom:uid", result.getIdentifier().get(0).getSystem());
        assertEquals(STUDY_INSTANCE_UID, result.getIdentifier().get(0).getValue());

        assertNotNull(result.getSubject());
        assertEquals("Patient/" + PATIENT_UUID, result.getSubject().getReference());

        assertNotNull(result.getBasedOn());
        assertEquals(1, result.getBasedOn().size());
        assertEquals("ServiceRequest/" + ORDER_UUID, result.getBasedOn().get(0).getReference());

        assertNull(result.getLocation());
        assertNull(result.getDescription());
    }

    @Test
    public void shouldBuildFhirPayloadWithCorrectReferencePrefixes() {
        FhirImagingStudy result = imagingStudyMapper.buildFhirPayload(
                ORDER_UUID,
                PATIENT_UUID,
                LOCATION_UUID,
                STUDY_INSTANCE_UID,
                DESCRIPTION
        );

        assertNotNull(result);

        assertTrue(result.getSubject().getReference().startsWith("Patient/"));
        assertTrue(result.getBasedOn().get(0).getReference().startsWith("ServiceRequest/"));
        assertTrue(result.getLocation().getReference().startsWith("Location/"));
    }

    @Test
    public void shouldSetStatusToUnknown() {
        FhirImagingStudy result = imagingStudyMapper.buildFhirPayload(
                ORDER_UUID,
                PATIENT_UUID,
                LOCATION_UUID,
                STUDY_INSTANCE_UID,
                DESCRIPTION
        );

        assertNotNull(result);
        assertEquals("unknown", result.getStatus());
    }

    @Test
    public void shouldSetResourceTypeToImagingStudy() {
        FhirImagingStudy result = imagingStudyMapper.buildFhirPayload(
                ORDER_UUID,
                PATIENT_UUID,
                LOCATION_UUID,
                STUDY_INSTANCE_UID,
                DESCRIPTION
        );

        assertNotNull(result);
        assertEquals("ImagingStudy", result.getResourceType());
    }

    @Test
    public void shouldSetIdentifierSystemToDicomUid() {
        FhirImagingStudy result = imagingStudyMapper.buildFhirPayload(
                ORDER_UUID,
                PATIENT_UUID,
                LOCATION_UUID,
                STUDY_INSTANCE_UID,
                DESCRIPTION
        );

        assertNotNull(result);
        assertNotNull(result.getIdentifier());
        assertEquals(1, result.getIdentifier().size());
        assertEquals("urn:dicom:uid", result.getIdentifier().get(0).getSystem());
    }
}
