/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.bahmni.module.pacsintegration.integrationtest;

import ca.uhn.hl7v2.*;
import ca.uhn.hl7v2.model.*;
import ca.uhn.hl7v2.model.v25.message.*;
import ca.uhn.hl7v2.model.v25.segment.*;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.BaseOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderConcept;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.Person;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.Provider;

import java.text.*;
import java.util.*;

public class HL7Utils {

    public static DateFormat getHl7DateFormat() {
        return new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public static MSH populateMessageHeader(MSH msh, Date dateTime, String messageType, String triggerEvent, String sendingFacility) throws DataTypeException {
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

    public static ORR_O02 generateORRwithAccept(String messageControlId, String sendingFacility) throws DataTypeException {
        ORR_O02 ack = new ORR_O02();

        populateMessageHeader(ack.getMSH(), new Date(), "ORR", "O02", sendingFacility);

        ack.getMSA().getAcknowledgmentCode().setValue(AcknowledgmentCode.AA.toString());
        ack.getMSA().getMessageControlID().setValue(messageControlId);

        return ack;
    }

    public static ORR_O02 generateORRWithError(String messageControlId, String sendingFacility, String errorMessage) throws DataTypeException {
        ORR_O02 ack = new ORR_O02();
        populateMessageHeader(ack.getMSH(), new Date(), "ORR", "002", sendingFacility);

        ack.getMSA().getAcknowledgmentCode().setValue(AcknowledgmentCode.AE.toString());
        ack.getMSA().getMessageControlID().setValue(messageControlId);
        ack.getMSA().getTextMessage().setValue(errorMessage);

        return ack;
    }

    public static ORM_O01 createORM_O01Message() {
        try {
            ORM_O01 message = new ORM_O01();
            message.getMSH().getFieldSeparator().setValue("|");
            message.getMSH().getEncodingCharacters().setValue("^~\\&");
            return message;
        } catch (HL7Exception e) {
            throw new RuntimeException("Failed to create test HL7 message", e);
        }
    }

    public static OpenMRSOrderDetails createScheduledOrderDetails() {
        OpenMRSOrderDetails orderDetails = new OpenMRSOrderDetails();
        orderDetails.setUuid("order-uuid");
        orderDetails.setOrderNumber("ORD-12345");
        orderDetails.setAction("NEW");
        orderDetails.setUrgency("ROUTINE");
        orderDetails.setDateCreated(new Date());
        return orderDetails;
    }

    public static OpenMRSOrderDetails createDiscontinuedOrderDetails() {
        OpenMRSOrderDetails orderDetails = new OpenMRSOrderDetails();
        orderDetails.setUuid("order-uuid");
        orderDetails.setOrderNumber("ORD-456");
        orderDetails.setAction("DISCONTINUE");
        orderDetails.setUrgency("ROUTINE");
        orderDetails.setDateCreated(new Date());

        BaseOrderDetails previousOrder = new BaseOrderDetails();
        previousOrder.setUuid("previous-uuid");
        previousOrder.setOrderNumber("PREV-ORD-123");
        previousOrder.setUrgency("ROUTINE");
        previousOrder.setDateCreated(new Date());

        orderDetails.setPreviousOrder(previousOrder);

        return orderDetails;
    }

    public static OpenMRSOrderDetails createOrderDetailsWithConcept() {
        OpenMRSOrderDetails orderDetails = createScheduledOrderDetails();

        OrderConcept concept = new OrderConcept();
        concept.setUuid("concept-uuid");
        orderDetails.setConcept(concept);

        return orderDetails;
    }

    public static OpenMRSOrderDetails createOrderDetailsWithProvider() {
        OpenMRSOrderDetails orderDetails = createOrderDetailsWithConcept();

        Provider provider = new Provider();
        provider.setUuid("provider-uuid");

        Person person = new Person();
        Person.PreferredName preferredName = new Person.PreferredName();
        preferredName.setGivenName("John");
        preferredName.setFamilyName("Doe");
        person.setPreferredName(preferredName);
        provider.setPerson(person);

        orderDetails.setCreator(provider);

        return orderDetails;
    }
}
