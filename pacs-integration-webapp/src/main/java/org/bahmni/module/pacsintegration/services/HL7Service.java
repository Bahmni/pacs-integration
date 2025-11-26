package org.bahmni.module.pacsintegration.services;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.group.ORM_O01_PATIENT;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.*;
import ca.uhn.hl7v2.util.Terser;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConceptMapping;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSProvider;
import org.bahmni.module.pacsintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.pacsintegration.exception.HL7MessageException;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class HL7Service {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StudyInstanceUIDGenerator studyInstanceUIDGenerator;

    @Value("${hl7.sending.application:Bahmni EMR}")
    private String sendingApplication;

    @Value("${hl7.sending.facility:Bahmni Hospital}")
    private String sendingFacility;

    @Value("${hl7.receiving.application:Bahmni PACS}")
    private String receivingApplication;

    @Value("${hl7.receiving.facility:Bahmni Hospital}")
    private String receivingFacility;

    @Value("${hl7.patient.identifier.type.code:MR}")
    private String patientIdentifierTypeCode;

    public HL7Service() {
    }


    private final String NEW_ORDER = "NW";
    private final String CANCEL_ORDER = "CA";
    private final String SENDER = "BahmniEMR";
    private final String HL7_SCHEDULED_STATUS_CODE = "SC";
    private final String HL7_CANCELLED_STATUS_CODE = "CA";
    private final String PATIENT_IDENTIFIER_AUTHORITY = "Bahmni EMR";

    public AbstractMessage createMessage(OpenMRSOrder order, OpenMRSPatient openMRSPatient, List<OpenMRSProvider> providers) throws HL7Exception {
        if (order.isDiscontinued()) {
            return cancelOrderMessage(order, openMRSPatient, providers);
        } else {
            return createOrderMessage(order, openMRSPatient, providers);
        }
    }

    private AbstractMessage createOrderMessage(OpenMRSOrder order, OpenMRSPatient openMRSPatient, List<OpenMRSProvider> providers) throws HL7Exception {
        ORM_O01 message = new ORM_O01();
        addMessageHeader(order, message);
        addPatientDetails(message, openMRSPatient);
        addProviderDetails(providers, message);

        // handle ORC component
        ORC orc = message.getORDER().getORC();
        String orderNumber = order.getOrderNumber();
        if (isSizeExceedingLimit(orderNumber)) {
            throw new HL7MessageException("Unable to create HL7 message. Order Number size exceeds limit " + orderNumber);
        }
        orc.getQuantityTiming(0).getPriority().setValue(order.getUrgency());
        orc.getPlacerOrderNumber().getEntityIdentifier().setValue(orderNumber);
        orc.getFillerOrderNumber().getEntityIdentifier().setValue(orderNumber); //accession number - should be of length 16 bytes
        orc.getEnteredBy(0).getGivenName().setValue(SENDER);
        orc.getOrderControl().setValue(NEW_ORDER);
        orc.getOrderStatus().setValue(HL7_SCHEDULED_STATUS_CODE);

        addOBRComponent(order, message);
        addZDSSegment(orderNumber, message);
        return message;
    }

    private boolean isSizeExceedingLimit(String orderNumber) {
        return orderNumber.getBytes().length > 16;
    }

    private AbstractMessage cancelOrderMessage(OpenMRSOrder order, OpenMRSPatient openMRSPatient, List<OpenMRSProvider> providers) throws DataTypeException, ca.uhn.hl7v2.HL7Exception {
        Order previousOrder = orderRepository.findByOrderUuid(order.getPreviousOrderUuid());
        if (previousOrder == null) {
            throw new HL7MessageException("Unable to Cancel the Order. Previous order is not found" + order.getOrderNumber());
        }
        ORM_O01 message = new ORM_O01();
        addMessageHeader(order, message);
        addPatientDetails(message, openMRSPatient);
        addProviderDetails(providers, message);

        // handle ORC component
        ORC orc = message.getORDER().getORC();
        String orderNumber = previousOrder.getOrderNumber();
        if (isSizeExceedingLimit(order.getOrderNumber())) {
            throw new HL7MessageException("Unable to create HL7 message. Order Number size exceeds limit" + orderNumber);
        }
        orc.getPlacerOrderNumber().getEntityIdentifier().setValue(orderNumber);
        orc.getFillerOrderNumber().getEntityIdentifier().setValue(orderNumber); //accession number - should be of length 16 bytes
        orc.getEnteredBy(0).getGivenName().setValue(SENDER);
        orc.getOrderControl().setValue(CANCEL_ORDER);
        orc.getOrderStatus().setValue(HL7_CANCELLED_STATUS_CODE);

        addOBRComponent(order, message);
        addZDSSegment(orderNumber, message);
        return message;
    }

    private void addMessageHeader(OpenMRSOrder order, ORM_O01 message) throws HL7Exception {
        MSH msh = message.getMSH();

        msh.getMessageControlID().setValue(generateMessageControlID(order.getOrderNumber()));
        populateMessageHeader(msh, new Date(), "ORM", "O01");
    }

    private void addOBRComponent(OpenMRSOrder order, ORM_O01 message) throws HL7Exception {
        // handle OBR component
        OBR obr = message.getORDER().getORDER_DETAIL().getOBR();

        OpenMRSConceptMapping pacsConceptSource = order.getPacsConceptSource();
        if (pacsConceptSource == null) {
            throw new HL7MessageException("Unable to create HL7 message. Missing concept source for order" + order.getUuid());
        }
        obr.getUniversalServiceIdentifier().getIdentifier().setValue(pacsConceptSource.getCode());
        obr.getUniversalServiceIdentifier().getText().setValue(pacsConceptSource.getName());
        obr.getReasonForStudy(0).getText().setValue(order.getCommentToFulfiller());
        obr.getCollectorSComment(0).getText().setValue(order.getConcept().getName().getName());
        obr.getPlacerField1().parse(order.getOrderNumber());
    }

    private void addProviderDetails(List<OpenMRSProvider> providers, ORM_O01 message) throws DataTypeException {
        OpenMRSProvider openMRSProvider = providers.get(0);
        ORC orc = message.getORDER().getORC();
        orc.getOrderingProvider(0).getGivenName().setValue(openMRSProvider.getName());
        orc.getOrderingProvider(0).getIDNumber().setValue(openMRSProvider.getUuid());
    }

    private void addPatientDetails(ORM_O01 message, OpenMRSPatient openMRSPatient) throws HL7Exception {
        // handle the patient PID component
        ORM_O01_PATIENT patient = message.getPATIENT();
        PID pid = patient.getPID();
        pid.getPatientIdentifierList(0).getIDNumber().setValue(openMRSPatient.getPatientId());
        pid.getPatientIdentifierList(0).getIdentifierTypeCode().setValue(patientIdentifierTypeCode);
        pid.getPatientIdentifierList(0).getAssigningAuthority().parse(PATIENT_IDENTIFIER_AUTHORITY);

        pid.getPatientName(0).getGivenName().setValue(openMRSPatient.getGivenName());
        pid.getPatientName(0).getFamilyName().getSurname().setValue(openMRSPatient.getFamilyName());
        pid.getDateTimeOfBirth().getTime().setValue(openMRSPatient.getBirthDate());
        pid.getAdministrativeSex().setValue(openMRSPatient.getGender());

        message.getORDER().getORDER_DETAIL().getOBR().getPlannedPatientTransportComment(0).getText().setValue(openMRSPatient.getGivenName() + "," + openMRSPatient.getFamilyName());

    }

    private static DateFormat getHl7DateFormat() {
        return new SimpleDateFormat("yyyyMMddHH");
    }

    private MSH populateMessageHeader(MSH msh, Date dateTime, String messageType, String triggerEvent) throws HL7Exception {
        msh.getFieldSeparator().setValue("|");
        msh.getEncodingCharacters().setValue("^~\\&");
        msh.getSendingApplication().parse(sendingApplication);
        msh.getSendingFacility().parse(sendingFacility);
        msh.getReceivingApplication().parse(receivingApplication);
        msh.getReceivingFacility().parse(receivingFacility);
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

    /**
     * Add ZDS segment for DICOM Study Instance UID (required by DCM4CHEE 5)
     * This method stores the StudyInstanceUID that will be added to the message during encoding
     */
    private void addZDSSegment(String orderNumber, ORM_O01 message) {
        try {
            String studyInstanceUID = studyInstanceUIDGenerator.generateStudyInstanceUID(orderNumber);
            message.addNonstandardSegment("ZDS");
            Terser terser = new Terser(message);
            terser.set("ZDS-1", studyInstanceUID);

        } catch (Exception e) {
            throw new RuntimeException("Unable to attach StudyInstanceUID for order", e);
        }
    }


}
