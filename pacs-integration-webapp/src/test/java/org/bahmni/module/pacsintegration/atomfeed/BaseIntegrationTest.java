package org.bahmni.module.pacsintegration.atomfeed;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import org.apache.log4j.Logger;
import org.bahmni.module.PacsIntegration;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PacsIntegration.class)
public abstract class BaseIntegrationTest {

    public static HL7Service modalityStubServer;
    private static final org.apache.log4j.Logger log = Logger.getLogger(BaseIntegrationTest.class);

    @Before
    public void startModalityStubServer() throws InterruptedException, UnknownHostException {
        HapiContext hapiContext = new DefaultHapiContext();
        int port = 6001;
        modalityStubServer = hapiContext.newServer(port, false);
        modalityStubServer.registerApplication("ORM", "O01", new ReceivingApplication() {
            @Override
            public Message processMessage(Message message, Map<String, Object> map) throws ReceivingApplicationException, HL7Exception {
                ORM_O01 ormMessage = (ORM_O01) message;
                try {
                    return ormMessage.generateACK();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public boolean canProcess(Message message) {
                return true;
            }
        });
        modalityStubServer.startAndWait();
        System.setProperty("ca.uhn.hl7v2.app.initiator.timeout", Integer.toString(2000));
        log.debug("Starting modality stub modalityStubServer at " + Inet4Address.getLocalHost().getHostAddress() + ":" + port + " with timeout of " + 2000);
    }

    @After
    public void stopModalityStubServer(){
        modalityStubServer.stopAndWait();
    }
}
