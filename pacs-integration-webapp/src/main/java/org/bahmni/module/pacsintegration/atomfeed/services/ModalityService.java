package org.bahmni.module.pacsintegration.atomfeed.services;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.ACK;
import ca.uhn.hl7v2.parser.PipeParser;
import org.bahmni.module.pacsintegration.atomfeed.exception.ModalityException;
import org.bahmni.module.pacsintegration.model.Modality;
import org.bahmni.module.pacsintegration.repository.OrderTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ModalityService {

    @Autowired
    private OrderTypeRepository orderTypeRepository;

    public void sendMessage(AbstractMessage message, String orderType) throws HL7Exception, LLPException, IOException {
        Modality modality = orderTypeRepository.getByName(orderType).getModality();
        Message response = post(modality, message);
        if (response instanceof ACK) {
            ACK acknowledgement = (ACK) response;
            String acknowledgmentCode = acknowledgement.getMSA().getAcknowledgmentCode().getValue();
            if (!AcknowledgmentCode.AA.toString().equals(acknowledgmentCode)) {
                String responseMessage = parseResponse(response);
                throw new ModalityException(responseMessage, modality);
            }
        }
    }

    Message post(Modality modality, Message requestMessage) throws LLPException, IOException, HL7Exception {
        Connection newClientConnection = null;
        try {
            HapiContext hapiContext = new DefaultHapiContext();
            newClientConnection = hapiContext.newClient(modality.getIp(), modality.getPort(), false);
            Initiator initiator = newClientConnection.getInitiator();
            return initiator.sendAndReceive(requestMessage);
        } finally {
            if (newClientConnection != null) {
                newClientConnection.close();
            }
        }
    }

    String parseResponse(Message response) throws HL7Exception {
        return new PipeParser().encode(response);
    }

}
