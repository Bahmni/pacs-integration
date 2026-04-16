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
public class JsonPatchOperation {
    
    private String op;
    private String path;
    private Object value;
    
    public JsonPatchOperation() {
    }
    
    public JsonPatchOperation(String op, String path, Object value) {
        this.op = op;
        this.path = path;
        this.value = value;
    }
    
    public String getOp() {
        return op;
    }
    
    public void setOp(String op) {
        this.op = op;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
}
