package org.bahmni.module.pacsintegration.psunotification;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.util.Terser;
import org.bahmni.module.pacsintegration.services.ImagingStudyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OMGO19MessageHandler implements ReceivingApplication {

    private static final Logger logger = LoggerFactory.getLogger(OMGO19MessageHandler.class);

    @Autowired
    private ImagingStudyService imagingStudyService;

    @Override
    public Message processMessage(Message message, Map<String, Object> metadata)
            throws HL7Exception {
        try {
            String studyInstanceUID = extractStudyInstanceUID(message);
            imagingStudyService.updateImagingStudyAsAvailable(studyInstanceUID);
            return message.generateACK();
        } catch (IOException e) {
            throw new HL7Exception("Failed to generate ACK", e);
        }
    }

    //TODO: Resolve this message once DCM4 provide the fix
    private String extractStudyInstanceUID(Message message) {
       try {
           Terser terser = new Terser(message);
           String identifier = terser.get("/OBX-3-1");
           String studyUID = terser.get("/OBX-5");
           logger.info("HL7 message identifier : {}, studyUID: {}", identifier, studyUID);
           return studyUID;
       } catch (HL7Exception e) {
           logger.error("Error extracting studyInstanceUID HL7: {}", e.getMessage(), e);
           return null;
       }
    }

    @Override
    public boolean canProcess(Message message) {
        return true;
    }
}
