package org.bahmni.module.pacsintegration.services;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.v25.message.ACK;
import org.bahmni.module.pacsintegration.exception.ModalityException;
import org.bahmni.module.pacsintegration.model.Modality;
import org.bahmni.module.pacsintegration.model.OrderType;
import org.bahmni.module.pacsintegration.repository.OrderTypeRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModalityServiceTest {

    @Mock
    public OrderTypeRepository orderTypeRepository;

    @Mock
    public AbstractMessage requestMessage;

    @Spy
    @InjectMocks
    public ModalityService modalityService = new ModalityService();
    private ACK acknowledgement;
    private String orderTypeName;
    private OrderType orderType;

    @Before
    public void setup() {
        orderTypeName = "Radiology";
        orderType = new OrderType();
        orderType.setModality(new Modality());
        acknowledgement = new ACK();
    }

    @Test
    public void shouldSendMessageSuccessfullyToModality() throws LLPException, IOException, HL7Exception {
        acknowledgement.getMSA().getAcknowledgmentCode().setValue(AcknowledgmentCode.AA.toString());
        when(orderTypeRepository.getByName(orderTypeName)).thenReturn(orderType);
        doReturn(acknowledgement).when(modalityService).post(orderType.getModality(), requestMessage);

        try {
            modalityService.sendMessage(requestMessage, orderTypeName);
        } catch (Exception e) {
            Assert.fail("Should not throw exception");
        }
    }

    @Test(expected = ModalityException.class)
    public void shouldThrowExceptionIfTheModalityRejectsTheMessage() throws HL7Exception, LLPException, IOException {
        acknowledgement.getMSA().getAcknowledgmentCode().setValue(AcknowledgmentCode.AR.toString());
        when(orderTypeRepository.getByName(orderTypeName)).thenReturn(orderType);
        doReturn(acknowledgement).when(modalityService).post(orderType.getModality(), requestMessage);
        doReturn("Failure").when(modalityService).parseResponse(acknowledgement);

        modalityService.sendMessage(requestMessage, orderTypeName);
    }

}