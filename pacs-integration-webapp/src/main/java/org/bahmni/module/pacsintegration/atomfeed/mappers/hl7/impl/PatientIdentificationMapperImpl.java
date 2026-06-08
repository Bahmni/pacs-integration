package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.datatype.CX;
import ca.uhn.hl7v2.model.v25.segment.PID;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.Person;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.Constants;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.PatientIdentificationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PatientIdentificationMapperImpl implements PatientIdentificationMapper {

    private static final Logger logger = LoggerFactory.getLogger(PatientIdentificationMapperImpl.class);

    @Override
    public void map(PID pid, OpenMRSOrderDetails orderDetails) {
        try {
            mapPatientDemographics(pid, orderDetails);
            mapPatientId(pid, orderDetails);

        } catch (HL7Exception e) {
            logger.error("Error mapping PID segment for patient", e);
            throw new RuntimeException("Failed to map patient identification segment", e);
        }
    }

    private void mapPatientDemographics(PID pid, OpenMRSOrderDetails orderDetails) throws DataTypeException {
        Person patientPerson = orderDetails.getPatient().getPerson();
        Person.PreferredName preferredName = patientPerson.getPreferredName();
        pid.getPatientName(0).getGivenName().setValue(preferredName.getGivenName());
        pid.getPatientName(0).getFamilyName().getSurname().setValue(preferredName.getFamilyName());

        pid.getDateTimeOfBirth().getTime().setValue(patientPerson.getBirthdate());
        pid.getAdministrativeSex().setValue(patientPerson.getGender());
    }

    private void mapPatientId(PID pid, OpenMRSOrderDetails orderDetails) throws HL7Exception {
        CX patientIdentifier = pid.getPatientIdentifierList(0);
        patientIdentifier.getIDNumber().setValue(orderDetails.getPatient().getPatientIdentifier().getIdentifier());
        patientIdentifier.getIdentifierTypeCode().setValue(Constants.PATIENT_IDENTIFIER_TYPE_CODE);
        patientIdentifier.getAssigningAuthority().parse(Constants.PATIENT_IDENTIFIER_ASSIGNING_AUTHORITY);

    }
}
