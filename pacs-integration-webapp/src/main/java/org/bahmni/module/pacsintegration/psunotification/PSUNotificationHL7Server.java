package org.bahmni.module.pacsintegration.psunotification;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.util.idgenerator.InMemoryIDGenerator;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
public class PSUNotificationHL7Server implements SmartLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(PSUNotificationHL7Server.class);

    @Value("${psu.notification.hl7.server.port:2576}")
    private int port;

    private HL7Service server;
    private volatile boolean running = false;

    @Autowired
    private OMGO19MessageHandler omgo19MessageHandler;

    @Override
    public void start() {
        try {
            startHL7Server();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start HL7 server", e);
        }
    }

    @Override
    public void stop() {
        startHL7Server();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable runnable) {
        stopHL7Server();
    }

    private void startHL7Server() {
        HapiContext context = new DefaultHapiContext();
        context.getParserConfiguration().setIdGenerator(new InMemoryIDGenerator());
        context.setValidationContext(new NoValidation());

        server = context.newServer(port, false);
        server.registerApplication("OMG", "O19", omgo19MessageHandler);
        server.start();
        running = true;
        logger.info("HL7 server for listening to PSU Notification started on port {}", port);
    }

    private void stopHL7Server() {
        if (server != null) {
            server.stop();
        }
        running = false;
    }
}
