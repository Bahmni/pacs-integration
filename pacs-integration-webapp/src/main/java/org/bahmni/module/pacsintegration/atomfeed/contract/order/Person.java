/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.pacsintegration.atomfeed.contract.order;

import lombok.Data;

import java.util.Date;

@Data
public class Person {
    private String gender;
    private Integer age;
    private Date birthdate;
    private PreferredName preferredName;


    @Data
    public static class PreferredName {
        private String givenName;
        private String middleName;
        private String familyName;
    }
}

