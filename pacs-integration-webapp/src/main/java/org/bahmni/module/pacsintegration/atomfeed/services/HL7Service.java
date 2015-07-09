package org.bahmni.module.pacsintegration.atomfeed.services;

import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.group.ORM_O01_PATIENT;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.*;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConceptMapping;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSProvider;
import org.bahmni.module.pacsintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.pacsintegration.atomfeed.exception.HL7MessageException;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class HL7Service {

    private final String NEW_ORDER = "NW";
    private final String SENDER = "BahmniEMR";

    public AbstractMessage createMessage(OpenMRSOrder order, OpenMRSPatient openMRSPatient, List<OpenMRSProvider> providers) throws DataTypeException {
        ORM_O01 message = new ORM_O01();
        MSH msh = message.getMSH();

        msh.getMessageControlID().setValue(generateMessageControlID(order.getOrderNumber()));
        populateMessageHeader(msh, new Date(), "ORM", "O01", SENDER);

        // handle the patient PID component
        ORM_O01_PATIENT patient = message.getPATIENT();
        PID pid = patient.getPID();
        pid.getPatientID().getIDNumber().setValue(openMRSPatient.getPatientId());
        pid.getPatientIdentifierList(0).getIDNumber().setValue(openMRSPatient.getPatientId());
        pid.getPatientName(0).getFamilyName().getSurname().setValue(openMRSPatient.getFamilyName());
        pid.getPatientName(0).getGivenName().setValue(openMRSPatient.getGivenName());
        pid.getDateTimeOfBirth().getTime().setValue(openMRSPatient.getBirthDate());
        pid.getAdministrativeSex().setValue(openMRSPatient.getGender());

        OpenMRSProvider openMRSProvider = providers.get(0);
        PV1 pv1 = message.getPATIENT().getPATIENT_VISIT().getPV1();
        pv1.getReferringDoctor(0).getIDNumber().setValue(openMRSProvider.getUuid());
        pv1.getReferringDoctor(0).getGivenName().setValue(openMRSProvider.getName());

        // handle ORC component
        ORC orc = message.getORDER().getORC();
        orc.getPlacerOrderNumber().getEntityIdentifier().setValue(order.getUuid());
        orc.getFillerOrderNumber().getEntityIdentifier().setValue(order.getUuid());
        orc.getEnteredBy(0).getGivenName().setValue(SENDER);
        orc.getOrderControl().setValue(NEW_ORDER);

        // handle OBR component
        OBR obr = message.getORDER().getORDER_DETAIL().getOBR();

        OpenMRSConceptMapping pacsConceptSource = order.getPacsConceptSource();
        if(pacsConceptSource == null) {
            throw new HL7MessageException("Unable to create HL7 message. Missing concept source for order" + order.getUuid());
        }
        obr.getUniversalServiceIdentifier().getIdentifier().setValue(pacsConceptSource.getCode());
        obr.getUniversalServiceIdentifier().getText().setValue(pacsConceptSource.getName());

        return message;
    }


    private static DateFormat getHl7DateFormat() {
        return new SimpleDateFormat("yyyyMMddHHmmss");
    }

    private MSH populateMessageHeader(MSH msh, Date dateTime, String messageType, String triggerEvent, String sendingFacility) throws DataTypeException {
        msh.getFieldSeparator().setValue("|");
        msh.getEncodingCharacters().setValue("^~\\&");
        msh.getSendingFacility().getHd1_NamespaceID().setValue(sendingFacility);
        msh.getSendingFacility().getUniversalID().setValue(sendingFacility);
        msh.getSendingFacility().getNamespaceID().setValue(sendingFacility);
        msh.getDateTimeOfMessage().getTs1_Time().setValue(getHl7DateFormat().format(dateTime));
        msh.getMessageType().getMessageCode().setValue(messageType);
        msh.getMessageType().getTriggerEvent().setValue(triggerEvent);
        //  TODO: do we need to send Message Control ID?
        msh.getProcessingID().getProcessingID().setValue("P");  // stands for production (?)
        msh.getVersionID().getVersionID().setValue("2.5");
        return msh;
    }

    String generateMessageControlID(String orderNumber) {
        int endAt = (orderNumber.length() < 9) ? orderNumber.length() : 9;
        return (new Date().getTime() + orderNumber.substring(4, endAt));
    }

}
