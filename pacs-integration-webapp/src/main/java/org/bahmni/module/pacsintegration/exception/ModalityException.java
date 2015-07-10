package org.bahmni.module.pacsintegration.exception;

import org.bahmni.module.pacsintegration.model.Modality;

public class ModalityException extends RuntimeException {

    private String responseMessage;
    private Modality modality;

    public ModalityException(String responseMessage, Modality modality) {
        super();
        this.responseMessage = responseMessage;
        this.modality = modality;
    }

    public String getMessage() {
        return "Unable to send the message to the modality \n" + modality.toString() + "\n" + responseMessage;
    }
}
