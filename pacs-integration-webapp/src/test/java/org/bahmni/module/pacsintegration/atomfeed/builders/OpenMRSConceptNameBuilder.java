/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.pacsintegration.atomfeed.builders;

import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConceptName;

public class OpenMRSConceptNameBuilder {
    private OpenMRSConceptName openMRSConceptName;

    public OpenMRSConceptNameBuilder() {
        this.openMRSConceptName = new OpenMRSConceptName();
    }

    public OpenMRSConceptNameBuilder withName(String name) {
        openMRSConceptName.setName(name);
        return this;
    }

    public OpenMRSConceptName build() {
        return openMRSConceptName;
    }
}