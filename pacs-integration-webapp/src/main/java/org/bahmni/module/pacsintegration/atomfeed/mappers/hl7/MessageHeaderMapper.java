/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7;

import ca.uhn.hl7v2.model.v25.segment.MSH;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;

public interface MessageHeaderMapper {
    void map(MSH messageHeader, OpenMRSOrderDetails orderDetails);
}
