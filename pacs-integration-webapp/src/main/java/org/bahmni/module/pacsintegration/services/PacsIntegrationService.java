package org.bahmni.module.pacsintegration.services;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.AbstractMessage;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderLocationInfo;
import org.bahmni.module.pacsintegration.atomfeed.mappers.OpenMRSEncounterToOrderMapper;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.model.OrderDetails;
import org.bahmni.module.pacsintegration.model.OrderType;
import org.bahmni.module.pacsintegration.model.ImagingStudyReference;
import org.bahmni.module.pacsintegration.repository.ImagingStudyReferenceRepository;
import org.bahmni.module.pacsintegration.repository.OrderDetailsRepository;
import org.bahmni.module.pacsintegration.repository.OrderRepository;
import org.bahmni.module.pacsintegration.repository.OrderTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class PacsIntegrationService {
    private static final Logger logger = LoggerFactory.getLogger(PacsIntegrationService.class);

    @Value("${create.imagingstudy.enabled}")
    private boolean imagingStudyEnabled;

    @Autowired
    private OpenMRSEncounterToOrderMapper openMRSEncounterToOrderMapper;

    @Autowired
    private OpenMRSService openMRSService;

    @Autowired
    private HL7Service hl7Service;

    @Autowired
    private StudyInstanceUIDGenerator studyInstanceUIDGenerator;

    @Autowired
    private OrderTypeRepository orderTypeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private ModalityService modalityService;

    @Autowired
    private HL7MessageCreator hl7MessageCreator;

    @Autowired
    private ImagingStudyService imagingStudyService;

    @Autowired
    private LocationResolver locationResolver;

    @Autowired
    private ImagingStudyReferenceRepository imagingStudyReferenceRepository;

    public void processEncounter(OpenMRSEncounter openMRSEncounter) throws IOException, HL7Exception, LLPException {
        List<OrderType> acceptableOrderTypes = orderTypeRepository.findAll();

        List<OpenMRSOrder> newAcceptableTestOrders = openMRSEncounter.getAcceptableTestOrders(acceptableOrderTypes);
        Collections.reverse(newAcceptableTestOrders);
        for(OpenMRSOrder openMRSOrder : newAcceptableTestOrders) {
            Order existingOrder = orderRepository.findByOrderUuid(openMRSOrder.getUuid());
            boolean needsProcessing = existingOrder == null || shouldCreateImagingStudy(existingOrder);

            if (needsProcessing) {
                Order order = existingOrder;
                OpenMRSOrderDetails orderDetails = null;

                if (existingOrder == null) {
                    orderDetails = openMRSService.getOrderDetails(openMRSOrder.getUuid());
                    AbstractMessage request = hl7MessageCreator.createHL7Message(orderDetails);
                    String response = modalityService.sendMessage(request, openMRSOrder.getOrderType());
                    order = openMRSEncounterToOrderMapper.map(openMRSOrder, openMRSEncounter, acceptableOrderTypes);

                    orderRepository.save(order);
                    orderDetailsRepository.save(new OrderDetails(order, request.encode(), response));
                    logger.info("Order created successfully for UUID: {}", openMRSOrder.getUuid());
                } else {
                    logger.info("Order already exists for UUID: {}, checking ImagingStudy status", openMRSOrder.getUuid());
                }

                if (shouldCreateImagingStudy(order)) {
                    if (orderDetails == null) {
                        orderDetails = openMRSService.getOrderDetails(openMRSOrder.getUuid());
                    }
                    
                    OrderLocationInfo orderLocationInfo = locationResolver.resolveLocations(orderDetails);
                    String studyInstanceUID = studyInstanceUIDGenerator.generateStudyInstanceUID(
                            openMRSOrder.getOrderNumber(), openMRSOrder.getDateCreated());

                    String imagingStudyUuid = imagingStudyService.createImagingStudy(
                            openMRSOrder.getUuid(),
                            openMRSEncounter.getPatientUuid(),
                            orderLocationInfo.getSourceLocation().getUuid(),
                            studyInstanceUID,
                            "Imaging Study for " + openMRSOrder.getTestName()
                    );
                    
                    logger.info("ImagingStudy created successfully with UUID: {} for Order: {}", 
                            imagingStudyUuid, openMRSOrder.getUuid());
                }
            }
        }
    }

    private boolean shouldCreateImagingStudy(Order order) {
        if (!imagingStudyEnabled || order == null) {
            return false;
        }

        List<ImagingStudyReference> references = imagingStudyReferenceRepository.findByOrderId(order.getId());
        return references == null || references.isEmpty();
    }

}
