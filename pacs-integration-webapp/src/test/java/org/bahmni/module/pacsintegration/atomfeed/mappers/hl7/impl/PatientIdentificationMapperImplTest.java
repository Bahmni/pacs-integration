package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.datatype.CX;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.PID;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.Patient;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.Person;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PatientIdentificationMapperImplTest {

    private PatientIdentificationMapperImpl patientIdentificationMapper;
    private PID pid;
    private SimpleDateFormat dateFormat;

    @Before
    public void setUp() throws HL7Exception {
        patientIdentificationMapper = new PatientIdentificationMapperImpl();

        ORM_O01 message = new ORM_O01();
        message.getMSH().getFieldSeparator().setValue("|");
        message.getMSH().getEncodingCharacters().setValue("^~\\&");
        pid = message.getPATIENT().getPID();

        dateFormat = new SimpleDateFormat("yyyyMMdd");
    }

    @Test
    public void shouldMapPatientDemographicsWithAllFields() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createOrderDetailsWithFullPatientInfo();

        patientIdentificationMapper.map(pid, orderDetails);

        assertEquals("John", pid.getPatientName(0).getGivenName().getValue());
        assertEquals("Doe", pid.getPatientName(0).getFamilyName().getSurname().getValue());
        assertTrue(pid.getDateTimeOfBirth().getTime().getValue().startsWith("19850315"));
        assertEquals("M", pid.getAdministrativeSex().getValue());
    }

    @Test
    public void shouldMapPatientIdentifierWithAllFields() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createOrderDetailsWithFullPatientInfo();

        patientIdentificationMapper.map(pid, orderDetails);

        CX patientIdentifier = pid.getPatientIdentifierList(0);
        assertEquals("PAT-123456", patientIdentifier.getIDNumber().getValue());
        assertEquals(Constants.PATIENT_IDENTIFIER_TYPE_CODE, patientIdentifier.getIdentifierTypeCode().getValue());
        assertEquals(Constants.PATIENT_IDENTIFIER_ASSIGNING_AUTHORITY,
            patientIdentifier.getAssigningAuthority().getNamespaceID().getValue());
    }

    @Test
    public void shouldMapFemalePatientsCorrectly() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createOrderDetailsWithFullPatientInfo();
        orderDetails.getPatient().getPerson().setGender("F");

        patientIdentificationMapper.map(pid, orderDetails);

        assertEquals("F", pid.getAdministrativeSex().getValue());
    }

    @Test
    public void shouldUseConstantsForIdentifierTypeCode() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createOrderDetailsWithFullPatientInfo();

        patientIdentificationMapper.map(pid, orderDetails);

        assertEquals(Constants.PATIENT_IDENTIFIER_TYPE_CODE, pid.getPatientIdentifierList(0).getIdentifierTypeCode().getValue());
    }

    @Test
    public void shouldUseConstantsForAssigningAuthority() throws HL7Exception {
        OpenMRSOrderDetails orderDetails = createOrderDetailsWithFullPatientInfo();

        patientIdentificationMapper.map(pid, orderDetails);

        assertEquals(Constants.PATIENT_IDENTIFIER_ASSIGNING_AUTHORITY,
            pid.getPatientIdentifierList(0).getAssigningAuthority().getNamespaceID().getValue());
    }

    private OpenMRSOrderDetails createOrderDetailsWithFullPatientInfo() {
        OpenMRSOrderDetails orderDetails = new OpenMRSOrderDetails();
        orderDetails.setUuid("order-uuid-123");
        orderDetails.setOrderNumber("ORD-12345");

        Patient patient = new Patient();
        patient.setUuid("patient-uuid-123");

        Patient.PatientIdentifier patientIdentifier = new Patient.PatientIdentifier();
        patientIdentifier.setIdentifier("PAT-123456");
        patient.setPatientIdentifier(patientIdentifier);

        Person person = new Person();
        person.setGender("M");
        person.setBirthdate(createDate(1985, 3, 15));

        Person.PreferredName preferredName = new Person.PreferredName();
        preferredName.setGivenName("John");
        preferredName.setMiddleName("Michael");
        preferredName.setFamilyName("Doe");
        person.setPreferredName(preferredName);

        patient.setPerson(person);
        orderDetails.setPatient(patient);

        return orderDetails;
    }

    private Date createDate(int year, int month, int day) {
        try {
            String dateString = String.format("%04d%02d%02d", year, month, day);
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            throw new RuntimeException("Error creating test date", e);
        }
    }
}
