package org.bahmni.module.pacsintegration.psunotification;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class OMGO19MessageHandler implements ReceivingApplication {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public Message processMessage(Message message, Map<String, Object> metadata)
            throws ReceivingApplicationException, HL7Exception {

        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);

        System.out.println();
        System.out.println("============ HL7 MESSAGE RECEIVED ============");
        System.out.println("Timestamp: " + timestamp);
        System.out.println("----------------------------------------------");

        try {
            // Print the raw message
            String rawMessage = message.encode();
            System.out.println("Raw Message:");
            System.out.println(rawMessage.replace("\r", "\n"));
            System.out.println("----------------------------------------------");

            // Print some metadata if available
            if (metadata != null && !metadata.isEmpty()) {
                System.out.println("Metadata:");
                for (Map.Entry<String, Object> entry : metadata.entrySet()) {
                    System.out.println("  " + entry.getKey() + ": " + entry.getValue());
                }
                System.out.println("----------------------------------------------");
            }

        } catch (HL7Exception e) {
            System.err.println("Error encoding message: " + e.getMessage());
        }

        System.out.println("==============================================");
        System.out.println();

        // Generate and return an ACK response
        try {
            return message.generateACK();
        } catch (IOException e) {
            throw new HL7Exception("Failed to generate ACK", e);
        }
    }

    @Override
    public boolean canProcess(Message message) {
        return true;
    }
}
