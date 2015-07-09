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
        Assert.assertEquals("Test NoOpenVisit", patient.getGivenName());
        Assert.assertEquals("Scenario", patient.getFamilyName());
        Assert.assertEquals("F", patient.getGender());
        Assert.assertEquals("Wed Jun 12 00:00:00 IST 1996", patient.getBirthDate().toString());

    }
}