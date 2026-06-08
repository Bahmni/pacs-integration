package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.util.Terser;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.ZDSMapper;
import org.bahmni.module.pacsintegration.services.StudyInstanceUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ZDSMapperImpl implements ZDSMapper {

    private static final Logger logger = LoggerFactory.getLogger(ZDSMapperImpl.class);

    private final StudyInstanceUIDGenerator studyInstanceUIDGenerator;

    @Autowired
    public ZDSMapperImpl(StudyInstanceUIDGenerator studyInstanceUIDGenerator) {
        this.studyInstanceUIDGenerator = studyInstanceUIDGenerator;
    }

    @Override
    public void mapStudyInstanceUID(ORM_O01 message, String orderNumber, Date dateCreated) {
        try {
            String studyInstanceUID = studyInstanceUIDGenerator.generateStudyInstanceUID(orderNumber, dateCreated);
            logger.info(String.format("Study Instance UID for Order %s is %s ", orderNumber, studyInstanceUID));
            message.addNonstandardSegment("ZDS");
            Terser terser = new Terser(message);
            terser.set("ZDS-1", studyInstanceUID);
        } catch (Exception e) {
            throw new RuntimeException("Unable to attach StudyInstanceUID for order", e);
        }
    }
}
