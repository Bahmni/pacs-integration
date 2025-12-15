package org.bahmni.module.pacsintegration.services;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.AbstractMessage;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderLocationInfo;
import org.bahmni.module.pacsintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.pacsintegration.atomfeed.mappers.OpenMRSEncounterToOrderMapper;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.model.OrderDetails;
import org.bahmni.module.pacsintegration.model.OrderType;
import org.bahmni.module.pacsintegration.repository.OrderDetailsRepository;
import org.bahmni.module.pacsintegration.repository.OrderRepository;
import org.bahmni.module.pacsintegration.repository.OrderTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
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
    private ImagingStudyService imagingStudyService;

    @Autowired
    private LocationResolver locationResolver;

    public void processEncounter(OpenMRSEncounter openMRSEncounter) throws IOException, ParseException, HL7Exception, LLPException {
        OpenMRSPatient patient = openMRSService.getPatient(openMRSEncounter.getPatientUuid());
        List<OrderType> acceptableOrderTypes = orderTypeRepository.findAll();

        List<OpenMRSOrder> newAcceptableTestOrders = openMRSEncounter.getAcceptableTestOrders(acceptableOrderTypes);
        Collections.reverse(newAcceptableTestOrders);
        for(OpenMRSOrder openMRSOrder : newAcceptableTestOrders) {
            if(orderRepository.findByOrderUuid(openMRSOrder.getUuid()) == null) {
                AbstractMessage request = hl7Service.createMessage(openMRSOrder, patient, openMRSEncounter.getProviders());
                String response = modalityService.sendMessage(request, openMRSOrder.getOrderType());
                Order order = openMRSEncounterToOrderMapper.map(openMRSOrder, openMRSEncounter, acceptableOrderTypes);

                orderRepository.save(order);
                orderDetailsRepository.save(new OrderDetails(order, request.encode(),response));

                logger.info("Imagining study creation enabled: {}", imagingStudyEnabled);

                if (imagingStudyEnabled) {
                    OpenMRSOrderDetails orderDetails = openMRSService.getOrderDetails(openMRSOrder.getUuid());
                    OrderLocationInfo orderLocationInfo = locationResolver.resolveLocations(orderDetails);
                    String studyInstanceUID = studyInstanceUIDGenerator.generateStudyInstanceUID(openMRSOrder.getOrderNumber());

                    logger.info("Creating ImagingStudy for Order: {} with StudyInstanceUID: {}", openMRSOrder.getUuid(), studyInstanceUID);
                    imagingStudyService.createImagingStudy(
                            openMRSOrder.getUuid(),
                            openMRSEncounter.getPatientUuid(),
                            orderLocationInfo.getSourceLocation().getUuid(),
                            studyInstanceUID,
                            "Imaging Study for " + openMRSOrder.getTestName()
                    );
                }

            }
        }
    }

}
