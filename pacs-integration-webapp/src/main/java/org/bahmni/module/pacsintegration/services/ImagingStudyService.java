/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.pacsintegration.services;

import org.bahmni.module.pacsintegration.model.Order;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface ImagingStudyService {

    String createImagingStudy(
            Order order,
            String patientUuid, 
            String locationUuid, 
            String studyInstanceUID,
            String description) throws IOException;

    void updateImagingStudyAsAvailable(String studyInstanceUID) throws IOException;
}
