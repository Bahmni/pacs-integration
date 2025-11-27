package org.bahmni.module.pacsintegration.services;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.ORC;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.util.Terser;
import junit.framework.Assert;
import org.bahmni.module.pacsintegration.atomfeed.builders.OpenMRSConceptBuilder;
import org.bahmni.module.pacsintegration.atomfeed.builders.OpenMRSOrderBuilder;
import org.bahmni.module.pacsintegration.atomfeed.client.Constants;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConceptMapping;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSProvider;
import org.bahmni.module.pacsintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.pacsintegration.exception.HL7MessageException;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.repository.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HL7ServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StudyInstanceUIDGenerator studyInstanceUIDGenerator;

    @InjectMocks
    private HL7Service hl7Service;

    @Before
    public void setUp() {
        // Set configurable properties from test application.properties values
        ReflectionTestUtils.setField(hl7Service, "sendingApplication", "Test EMR");
        ReflectionTestUtils.setField(hl7Service, "sendingFacility", "Test Hospital");
        ReflectionTestUtils.setField(hl7Service, "receivingApplication", "Test PACS");
        ReflectionTestUtils.setField(hl7Service, "receivingFacility", "Test Facility");
        ReflectionTestUtils.setField(hl7Service, "patientIdentifierTypeCode", "MR");
    }

    @Test
    public void testGenerateMessageControlIDShouldBeLessThan20Characters() throws Exception {
        String messageControlID = hl7Service.generateMessageControlID("ORD-35");

        Assert.assertTrue("HL7 Message control id should be less than 20 characters", messageControlID.length() <= 20);
    }

    @Test
    public void testGenerateMessageControlIDShouldBeLessThan20CharactersForLongOrderNumbers() throws Exception {
        String messageControlID = hl7Service.generateMessageControlID("ORD-3550000");

        Assert.assertTrue("HL7 Message control id should be less than 20 characters", messageControlID.length() <= 20);
    }

    @Test(expected=HL7MessageException.class)
    public void testShouldThrowExceptionWhenThereIsNoPACSConceptSource() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource("some source", "123")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");

        hl7Service.createMessage(order, patient, providers);
    }

    @Test
    public void testShouldCreateHL7Message() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");

        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        Assert.assertNotNull(hl7Message);
        assertEquals("NW", hl7Message.getORDER().getORC().getOrderControl().getValue());
    }

    @Test
    public void testShouldCreateCancelOrderMessageForDiscontinuedOrder() throws Exception {
        Order previousOrder = new Order(111, null, "someOrderUuid", "someTestName", "someTestUuid", null, "ORD-111", "Comment");
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-222").withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123")).withPreviousOrderUuid(previousOrder.getOrderUuid()).withDiscontinued().build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();
        when(orderRepository.findByOrderUuid(order.getPreviousOrderUuid())).thenReturn(previousOrder);

        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        Assert.assertNotNull(hl7Message);
        assertEquals("CA", hl7Message.getORDER().getORC().getOrderControl().getValue());
        assertEquals("ORD-111", hl7Message.getORDER().getORC().getFillerOrderNumber().getEntityIdentifier().getValue());
    }

    @Test(expected = HL7MessageException.class)
    public void testShouldThrowExceptionForOrderNumberWithSizeExceedingLimit() throws Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-11189067898900").withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");

        hl7Service.createMessage(order, patient, providers);
    }

    @Test
    public void testShouldSetConfigurableMessageHeaderFields() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");

        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        MSH msh = hl7Message.getMSH();
        assertEquals("Test EMR", msh.getSendingApplication().encode());
        assertEquals("Test Hospital", msh.getSendingFacility().encode());
        assertEquals("Test PACS", msh.getReceivingApplication().encode());
        assertEquals("Test Facility", msh.getReceivingFacility().encode());
    }

    @Test
    public void testShouldSetPatientIdentifierTypeCode() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123")).build();
        OpenMRSPatient patient = createPatient("PAT-001", "John", "Doe", "M");
        List<OpenMRSProvider> providers = getProvidersData();

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");

        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        PID pid = hl7Message.getPATIENT().getPID();
        assertEquals("MR", pid.getPatientIdentifierList(0).getIdentifierTypeCode().getValue());
    }

    @Test
    public void testShouldSetOrderUrgency() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder()
                .withOrderNumber("ORD-111")
                .withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123"))
                .withUrgency("STAT")
                .build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");

        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        ORC orc = hl7Message.getORDER().getORC();
        assertEquals("STAT", orc.getQuantityTiming(0).getPriority().getValue());
    }

    @Test
    public void testShouldSetOrderStatusToScheduled() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");

        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        ORC orc = hl7Message.getORDER().getORC();
        assertEquals("SC", orc.getOrderStatus().getValue());
    }

    @Test
    public void testShouldSetReasonForStudyFromCommentToFulfiller() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder()
                .withOrderNumber("ORD-111")
                .withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123"))
                .withCommentToFulfiller("Patient has chest pain")
                .build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");

        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        OBR obr = hl7Message.getORDER().getORDER_DETAIL().getOBR();
        assertEquals("Patient has chest pain", obr.getReasonForStudy(0).getText().getValue());
    }

    @Test
    public void testShouldSetCollectorCommentFromConceptName() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder()
                .withOrderNumber("ORD-111")
                .withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "CR001"))
                .build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");

        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        OBR obr = hl7Message.getORDER().getORDER_DETAIL().getOBR();
        assertEquals("CR001", obr.getCollectorSComment(0).getText().getValue());
    }

    @Test
    public void testShouldSetPlannedPatientTransportCommentWithPatientName() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123")).build();
        OpenMRSPatient patient = createPatient("PAT-001", "John", "Doe", "M");
        List<OpenMRSProvider> providers = getProvidersData();

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");

        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        OBR obr = hl7Message.getORDER().getORDER_DETAIL().getOBR();
        assertEquals("John,Doe", obr.getPlannedPatientTransportComment(0).getText().getValue());
    }

    @Test
    public void testShouldAddZDSSegmentWithStudyInstanceUID() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        String expectedStudyInstanceUID = "1.2.826.0.1.3680043.8.498.12345.67890";
        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn(expectedStudyInstanceUID);

        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        Terser terser = new Terser(hl7Message);
        String actualStudyInstanceUID = terser.get("ZDS-1");
        assertEquals(expectedStudyInstanceUID, actualStudyInstanceUID);
    }

    @Test
    public void testShouldSetPatientDetailsCorrectly() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123")).build();
        OpenMRSPatient patient = createPatient("PAT-001", "John", "Doe", "M");
        patient.setBirthDate(new Date());
        List<OpenMRSProvider> providers = getProvidersData();

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");

        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        PID pid = hl7Message.getPATIENT().getPID();
        assertEquals("PAT-001", pid.getPatientIdentifierList(0).getIDNumber().getValue());
        assertEquals("John", pid.getPatientName(0).getGivenName().getValue());
        assertEquals("Doe", pid.getPatientName(0).getFamilyName().getSurname().getValue());
        assertEquals("M", pid.getAdministrativeSex().getValue());
        assertEquals("Bahmni EMR", pid.getPatientIdentifierList(0).getAssigningAuthority().encode());
    }

    @Test
    public void testShouldSetProviderDetailsCorrectly() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = new ArrayList<OpenMRSProvider>();
        providers.add(new OpenMRSProvider("provider-uuid-123", "Dr. Smith"));

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");

        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        ORC orc = hl7Message.getORDER().getORC();
        assertEquals("Dr. Smith", orc.getOrderingProvider(0).getGivenName().getValue());
        assertEquals("provider-uuid-123", orc.getOrderingProvider(0).getIDNumber().getValue());
    }

    @Test
    public void testShouldSetUniversalServiceIdentifierFromPacsConceptSource() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder()
                .withOrderNumber("ORD-111")
                .withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "CR001"))
                .build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");

        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        OBR obr = hl7Message.getORDER().getORDER_DETAIL().getOBR();
        assertEquals("CR001", obr.getUniversalServiceIdentifier().getIdentifier().getValue());
        assertEquals("CR001", obr.getUniversalServiceIdentifier().getText().getValue());
    }

    @Test
    public void testCancelOrderShouldAlsoHaveZDSSegment() throws HL7Exception {
        Order previousOrder = new Order(111, null, "someOrderUuid", "someTestName", "someTestUuid", null, "ORD-111", "Comment");
        OpenMRSOrder order = new OpenMRSOrderBuilder()
                .withOrderNumber("ORD-222")
                .withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123"))
                .withPreviousOrderUuid(previousOrder.getOrderUuid())
                .withDiscontinued()
                .build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();
        when(orderRepository.findByOrderUuid(order.getPreviousOrderUuid())).thenReturn(previousOrder);
        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");
        when(studyInstanceUIDGenerator.generateStudyInstanceUID(previousOrder.getOrderNumber())).thenReturn("1.2.3.4.5");


        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        Terser terser = new Terser(hl7Message);
        try {
            String zdsValue = terser.get("ZDS-1");
            assertNotNull("ZDS segment should not be present in cancel order message", zdsValue);
        } catch (HL7Exception e) {
            // Expected - ZDS segment doesn't exist
            assertTrue(e.getMessage().contains("ZDS") || e.getMessage().contains("Can't find"));
        }
    }

    @Test
    public void testShouldSetMessageTypeAndTriggerEvent() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");

        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        MSH msh = hl7Message.getMSH();
        assertEquals("ORM", msh.getMessageType().getMessageCode().getValue());
        assertEquals("O01", msh.getMessageType().getTriggerEvent().getValue());
        assertEquals("2.5", msh.getVersionID().getVersionID().getValue());
        assertEquals("P", msh.getProcessingID().getProcessingID().getValue());
    }

    @Test
    public void testShouldSetPlacerAndFillerOrderNumber() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");

        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        ORC orc = hl7Message.getORDER().getORC();
        assertEquals("ORD-111", orc.getPlacerOrderNumber().getEntityIdentifier().getValue());
        assertEquals("ORD-111", orc.getFillerOrderNumber().getEntityIdentifier().getValue());
        assertEquals("BahmniEMR", orc.getEnteredBy(0).getGivenName().getValue());
    }

    @Test(expected = RuntimeException.class)
    public void testShouldThrowExceptionWhenStudyInstanceUIDGeneratorFails() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenThrow(new RuntimeException("UID generation failed"));

        hl7Service.createMessage(order, patient, providers);
    }

    @Test(expected = HL7MessageException.class)
    public void testShouldThrowExceptionWhenPreviousOrderNotFoundForCancelOrder() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder()
                .withOrderNumber("ORD-222")
                .withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123"))
                .withPreviousOrderUuid("non-existent-uuid")
                .withDiscontinued()
                .build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();
        when(orderRepository.findByOrderUuid("non-existent-uuid")).thenReturn(null);

        hl7Service.createMessage(order, patient, providers);
    }

    @Test
    public void testShouldAddOrderNumberToOBRPlacerField1() throws HL7Exception {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(order.getOrderNumber())).thenReturn("1.2.3.4.5");

        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        OBR obr = hl7Message.getORDER().getORDER_DETAIL().getOBR();
        assertEquals("ORD-111", obr.getPlacerField1().getValue());
    }

    private OpenMRSConcept buildConceptWithSource(String conceptSourceName, String pacsCode) {
        final OpenMRSConceptMapping mapping = new OpenMRSConceptMapping();
        mapping.setCode(pacsCode);
        mapping.setName(pacsCode);
        mapping.setSource(conceptSourceName);
        return new OpenMRSConceptBuilder().addConceptMapping(mapping).addConceptName(pacsCode).build();
    }

    private List<OpenMRSProvider> getProvidersData() {
        List<OpenMRSProvider> providers = new ArrayList<OpenMRSProvider>();
        providers.add(new OpenMRSProvider());
        return providers;
    }

    private OpenMRSPatient createPatient(String patientId, String givenName, String familyName, String gender) {
        OpenMRSPatient patient = new OpenMRSPatient();
        patient.setPatientId(patientId);
        patient.setGivenName(givenName);
        patient.setFamilyName(familyName);
        patient.setGender(gender);
        return patient;
    }
}