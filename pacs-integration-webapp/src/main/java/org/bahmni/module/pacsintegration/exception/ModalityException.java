/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

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
