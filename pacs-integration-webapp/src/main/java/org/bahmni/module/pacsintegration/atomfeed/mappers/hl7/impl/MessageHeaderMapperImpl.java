package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderLocationInfo;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.Constants;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.MessageHeaderMapper;
import org.bahmni.module.pacsintegration.services.LocationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class MessageHeaderMapperImpl implements MessageHeaderMapper {

    private static final Logger logger = LoggerFactory.getLogger(MessageHeaderMapperImpl.class);
    private static final String DATETIME_FORMAT = "yyyyMMddHHmmss";

    private final String defaultSendingApplication;
    private final String defaultSendingFacility;
    private final String defaultReceivingApplication;
    private final String defaultReceivingFacility;
    private final LocationResolver locationResolver;

    @Autowired
    public MessageHeaderMapperImpl(@Value("${hl7.sending.application:Bahmni EMR}") String defaultSendingApplication,
                                    @Value("${hl7.sending.facility:Bahmni Hospital}") String defaultSendingFacility,
                                    @Value("${hl7.receiving.application:Bahmni PACS}") String defaultReceivingApplication,
                                    @Value("${hl7.receiving.facility:Bahmni Radiology}") String defaultReceivingFacility,
                                    LocationResolver locationResolver) {
        this.defaultSendingApplication = defaultSendingApplication;
        this.defaultSendingFacility = defaultSendingFacility;
        this.defaultReceivingApplication = defaultReceivingApplication;
        this.defaultReceivingFacility = defaultReceivingFacility;
        this.locationResolver = locationResolver;
    }

    @Override
    public void map(MSH messageHeader, OpenMRSOrderDetails orderDetails) {
        try {
            logger.debug("Mapping MSH segment for order number: {}", orderDetails.getOrderNumber());
            setMessageHeaderConstants(messageHeader);

            OrderLocationInfo orderLocationInfo = locationResolver.resolveLocations(orderDetails);

            messageHeader.getSendingApplication().getNamespaceID().setValue(defaultSendingApplication);
            messageHeader.getSendingFacility().parse(orderLocationInfo.getSourceLocation().getName());
            messageHeader.getReceivingApplication().getNamespaceID().setValue(defaultReceivingApplication);
            messageHeader.getReceivingFacility().getNamespaceID().setValue(orderLocationInfo.getFulfillingLocation().getName());

            messageHeader.getMessageControlID().setValue(generateMessageControlID(orderDetails.getOrderNumber()));

            logger.info("Successfully mapped MSH segment for order: {}", orderDetails.getOrderNumber());

        } catch (HL7Exception e) {
            logger.error("Error mapping MSH segment for order: {}", orderDetails.getOrderNumber(), e);
            throw new RuntimeException(e);
        }
    }

    private void setMessageHeaderConstants(MSH messageHeader) throws DataTypeException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIME_FORMAT);
        messageHeader.getDateTimeOfMessage().getTime().setValue(dateFormat.format(new Date()));
        messageHeader.getFieldSeparator().setValue(Constants.FIELD_SEPARATOR);
        messageHeader.getEncodingCharacters().setValue(Constants.ENCODING_CHARACTERS);
        messageHeader.getProcessingID().getProcessingID().setValue(Constants.HL7_PROCESSING_PROD_MODE);
        messageHeader.getVersionID().getVersionID().setValue(Constants.HL7_MESSAGE_VERSION);
        messageHeader.getMessageType().getMessageCode().setValue(Constants.HL7_MESSAGE_CODE);
        messageHeader.getMessageType().getTriggerEvent().setValue(Constants.HL7_TRIGGER_EVENT);
    }

    String generateMessageControlID(String orderNumber) {
        int endAt = (orderNumber.length() < 9) ? orderNumber.length() : 9;
        return (new Date().getTime() + orderNumber.substring(4, endAt));
    }
}
