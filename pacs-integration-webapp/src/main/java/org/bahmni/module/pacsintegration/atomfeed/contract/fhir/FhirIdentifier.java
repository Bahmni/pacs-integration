/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.pacsintegration.atomfeed.contract.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FhirIdentifier {
    
    private String system;
    private String value;
    
    public FhirIdentifier() {
    }
    
    public FhirIdentifier(String system, String value) {
        this.system = system;
        this.value = value;
    }
    
    public String getSystem() {
        return system;
    }
    
    public void setSystem(String system) {
        this.system = system;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}
