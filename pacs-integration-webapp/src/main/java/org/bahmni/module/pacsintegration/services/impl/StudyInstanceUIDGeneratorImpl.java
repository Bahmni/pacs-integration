/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.pacsintegration.services.impl;

import org.bahmni.module.pacsintegration.services.StudyInstanceUIDGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class StudyInstanceUIDGeneratorImpl implements StudyInstanceUIDGenerator {

    @Value("${study.instance.uid.prefix:1.2.826.0.1.3680043.8.498}")
    private String studyInstanceUIDPrefix;

    @Override
    public String generateStudyInstanceUID(String orderNumber, Date dateCreated) {

        int orderHash = Math.abs(orderNumber.hashCode());
        return studyInstanceUIDPrefix + "." + dateCreated.getTime() + '.' + orderHash;
    }
}
