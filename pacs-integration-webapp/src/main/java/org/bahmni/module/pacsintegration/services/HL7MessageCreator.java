/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.pacsintegration.services;

import ca.uhn.hl7v2.model.AbstractMessage;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;

public interface HL7MessageCreator {
    AbstractMessage createHL7Message(OpenMRSOrderDetails orderDetails);
}
