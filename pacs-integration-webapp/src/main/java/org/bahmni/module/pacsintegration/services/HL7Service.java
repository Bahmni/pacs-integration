package org.bahmni.module.pacsintegration.services;

import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.group.ORM_O01_PATIENT;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.*;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConceptMapping;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSProvider;
import org.bahmni.module.pacsintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.pacsintegration.exception.HL7MessageException;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class HL7Service {

    @Autowired
    private OrderRepository orderRepository;


    public HL7Service() {
    }

    public HL7Service(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    private final String NEW_ORDER = "NW";
    private final String CANCEL_ORDER = "CA";
    private final String SENDER = "BahmniEMR";

    public AbstractMessage createMessage(OpenMRSOrder order, OpenMRSPatient openMRSPatient, List<OpenMRSProvider> providers) throws DataTypeException {
        if(order.isDiscontinued()) {
            return cancelOrderMessage(order, openMRSPatient, providers);
        } else {
            return createOrderMessage(order, openMRSPatient, providers);
        }
    }

    private AbstractMessage createOrderMessage(OpenMRSOrder order, OpenMRSPatient openMRSPatient, List<OpenMRSProvider> providers) throws DataTypeException {
        ORM_O01 message = new ORM_O01();
        addMessageHeader(order, message);
        addPatientDetails(message, openMRSPatient);
        addProviderDetails(providers, message);

        // handle ORC component
        ORC orc = message.getORDER().getORC();
        String orderNumber = order.getOrderNumber();
        if(isSizeExceedingLimit(orderNumber)) {
            throw new HL7MessageException("Unable to create HL7 message. Order Number size exceeds limit " + orderNumber);
        }
        orc.getPlacerOrderNumber().getEntityIdentifier().setValue(orderNumber);
        orc.getFillerOrderNumber().getEntityIdentifier().setValue(orderNumber); //accession number - should be of length 16 bytes
        orc.getEnteredBy(0).getGivenName().setValue(SENDER);
        orc.getOrderControl().setValue(NEW_ORDER);

        addOBRComponent(order, message);
        return message;
    }

    private boolean isSizeExceedingLimit(String orderNumber) {
        return orderNumber.getBytes().length > 16;
    }

    private AbstractMessage cancelOrderMessage(OpenMRSOrder order, OpenMRSPatient openMRSPatient, List<OpenMRSProvider> providers) throws DataTypeException {
        Order previousOrder = orderRepository.findByOrderUuid(order.getPreviousOrderUuid());

        ORM_O01 message = new ORM_O01();
        addMessageHeader(order, message);
        addPatientDetails(message, openMRSPatient);
        addProviderDetails(providers, message);

        // handle ORC component
        ORC orc = message.getORDER().getORC();
        String orderNumber = previousOrder.getOrderNumber();
        if(isSizeExceedingLimit(order.getOrderNumber())) {
            throw new HL7MessageException("Unable to create HL7 message. Order Number size exceeds limit" + orderNumber);
        }
        orc.getPlacerOrderNumber().getEntityIdentifier().setValue(orderNumber);
        orc.getFillerOrderNumber().getEntityIdentifier().setValue(orderNumber); //accession number - should be of length 16 bytes
        orc.getEnteredBy(0).getGivenName().setValue(SENDER);
        orc.getOrderControl().setValue(CANCEL_ORDER);

        addOBRComponent(order, message);
        return message;
    }

    private void addMessageHeader(OpenMRSOrder order, ORM_O01 message) throws DataTypeException {
        MSH msh = message.getMSH();

        msh.getMessageControlID().setValue(generateMessageControlID(order.getOrderNumber()));
        populateMessageHeader(msh, new Date(), "ORM", "O01", SENDER);
    }

    private void addOBRComponent(OpenMRSOrder order, ORM_O01 message) throws DataTypeException {
        // handle OBR component
        OBR obr = message.getORDER().getORDER_DETAIL().getOBR();

        OpenMRSConceptMapping pacsConceptSource = order.getPacsConceptSource();
        if(pacsConceptSource == null) {
            throw new HL7MessageException("Unable to create HL7 message. Missing concept source for order" + order.getUuid());
        }
        obr.getUniversalServiceIdentifier().getIdentifier().setValue(pacsConceptSource.getCode());
        obr.getUniversalServiceIdentifier().getText().setValue(pacsConceptSource.getName());
       // obr.getReasonForStudy(0).getText().setValue(order.getInstructions()); //Notes - should not exceed 256 bits
    }

    private void addProviderDetails(List<OpenMRSProvider> providers, ORM_O01 message) throws DataTypeException {
        OpenMRSProvider openMRSProvider = providers.get(0);
        ORC orc = message.getORDER().getORC();
        orc.getOrderingProvider(0).getGivenName().setValue(openMRSProvider.getName());
        orc.getOrderingProvider(0).getIDNumber().setValue(openMRSProvider.getUuid());
    }

    private void addPatientDetails(ORM_O01 message, OpenMRSPatient openMRSPatient) throws DataTypeException {
        // handle the patient PID component
        ORM_O01_PATIENT patient = message.getPATIENT();
        PID pid = patient.getPID();
        pid.getPatientIdentifierList(0).getIDNumber().setValue(openMRSPatient.getPatientId());
        pid.getPatientName(0).getFamilyName().getSurname().setValue(openMRSPatient.getFamilyName());
        pid.getPatientName(0).getGivenName().setValue(openMRSPatient.getGivenName());
        pid.getDateTimeOfBirth().getTime().setValue(openMRSPatient.getBirthDate());
        pid.getAdministrativeSex().setValue(openMRSPatient.getGender());
    }

    private static DateFormat getHl7DateFormat() {
        return new SimpleDateFormat("yyyyMMddHH");
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
