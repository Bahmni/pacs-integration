/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.pacsintegration.atomfeed.mappers;

import junit.framework.Assert;
import org.bahmni.module.pacsintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.module.pacsintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.junit.Test;

public class OpenMRSPatientMapperTest extends OpenMRSMapperBaseTest {

    @Test
    public void testMap() throws Exception {
        String json = deserialize("/samplePatient.json");
        OpenMRSPatient patient = new OpenMRSPatientMapper().map(json);

        Assert.assertEquals("GAN200053", patient.getPatientId());
        Assert.assertEquals(" T e s t N o O p e_n-V i s i t ", patient.getGivenName());
        Assert.assertEquals("S c e n a r i o 9", patient.getFamilyName());
        Assert.assertEquals("F", patient.getGender());
//        Assert.assertEquals("Wed Jun 12 00:15:00 NPT 1996", patient.getBirthDate().toString());

    }
}