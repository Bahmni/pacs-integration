package org.bahmni.module.pacsintegration.services.impl;

import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.v25.group.ORM_O01_ORDER;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.MessageHeaderMapper;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.OBRMapper;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.ORCMapper;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.PatientIdentificationMapper;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.ZDSMapper;
import org.bahmni.module.pacsintegration.exception.HL7MessageException;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.repository.OrderRepository;
import org.bahmni.module.pacsintegration.services.HL7MessageCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HL7MessageCreatorImpl implements HL7MessageCreator {

    private final OrderRepository orderRepository;
    private final MessageHeaderMapper messageHeaderMapper;
    private final PatientIdentificationMapper patientIdentificationMapper;
    private final ORCMapper orcMapper;
    private final OBRMapper obrMapper;
    private final ZDSMapper zdsMapper;

    @Autowired
    public HL7MessageCreatorImpl(OrderRepository orderRepository,
                                  MessageHeaderMapper messageHeaderMapper,
                                  PatientIdentificationMapper patientIdentificationMapper,
                                  ORCMapper orcMapper,
                                  OBRMapper obrMapper,
                                  ZDSMapper zdsMapper) {
        this.orderRepository = orderRepository;
        this.messageHeaderMapper = messageHeaderMapper;
        this.patientIdentificationMapper = patientIdentificationMapper;
        this.orcMapper = orcMapper;
        this.obrMapper = obrMapper;
        this.zdsMapper = zdsMapper;
    }

    @Override
    public AbstractMessage createHL7Message(OpenMRSOrderDetails orderDetails) {
        ORM_O01 message = new ORM_O01();
        ORM_O01_ORDER ormOrder = message.getORDER();

        messageHeaderMapper.map(message.getMSH(), orderDetails);
        patientIdentificationMapper.map(message.getPATIENT().getPID(), orderDetails);
        obrMapper.map(ormOrder.getORDER_DETAIL().getOBR(), orderDetails);

        if (orderDetails.isDiscontinuedOrder()) {
            validatePreviousOrder(orderDetails);
            orcMapper.mapDiscontinuedOrder(ormOrder.getORC(), orderDetails);
            zdsMapper.mapStudyInstanceUID(message, orderDetails.getPreviousOrder().getOrderNumber(), orderDetails.getPreviousOrder().getDateCreated());
        } else {
            orcMapper.mapScheduledOrder(ormOrder.getORC(), orderDetails);
            zdsMapper.mapStudyInstanceUID(message, orderDetails.getOrderNumber(), orderDetails.getDateCreated());
        }

        return message;
    }

    private void validatePreviousOrder(OpenMRSOrderDetails orderDetails) {
        Order previousOrder = orderRepository.findByOrderUuid(orderDetails.getPreviousOrder().getUuid());
        if (previousOrder == null) {
            throw new HL7MessageException("Unable to Cancel the Order. Previous order is not found/processed" + orderDetails.getOrderNumber());
        }
    }
}
