package org.bahmni.module.pacsintegration.atomfeed.services;

import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.DataTypeException;
import junit.framework.Assert;
import org.bahmni.module.pacsintegration.atomfeed.builders.OpenMRSConceptBuilder;
import org.bahmni.module.pacsintegration.atomfeed.builders.OpenMRSOrderBuilder;
import org.bahmni.module.pacsintegration.atomfeed.client.Constants;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConceptMapping;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSProvider;
import org.bahmni.module.pacsintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.pacsintegration.atomfeed.exception.HL7MessageException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class HL7ServiceTest {


    @Test
    public void testGenerateMessageControlIDShouldBeLessThan20Characters() throws Exception {
        HL7Service hl7Service = new HL7Service();
        String messageControlID = hl7Service.generateMessageControlID("ORD-35");

        Assert.assertTrue("HL7 Message control id should be less than 20 characters", messageControlID.length() <= 20);
    }

    @Test
    public void testGenerateMessageControlIDShouldBeLessThan20CharactersForLongOrderNumbers() throws Exception {
        HL7Service hl7Service = new HL7Service();
        String messageControlID = hl7Service.generateMessageControlID("ORD-3550000");

        Assert.assertTrue("HL7 Message control id should be less than 20 characters", messageControlID.length() <= 20);
    }

    @Test(expected=HL7MessageException.class)
    public void testShouldThrowExceptionWhenThereIsNoPACSConceptSource() throws DataTypeException {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource("some source", "123")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        HL7Service hl7Service = new HL7Service();
        hl7Service.createMessage(order, patient, providers);
    }

    @Test
    public void testShouldCreateHL7Message() throws DataTypeException {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource(Constants.PACS_CONCEPT_SOURCE_NAME, "123")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        HL7Service hl7Service = new HL7Service();
        AbstractMessage hl7Message = hl7Service.createMessage(order, patient, providers);

        Assert.assertNotNull(hl7Message);
    }

    private OpenMRSConcept buildConceptWithSource(String conceptSourceName, String pacsCode) {
        final OpenMRSConceptMapping mapping = new OpenMRSConceptMapping();
        mapping.setCode(pacsCode);
        mapping.setSource(conceptSourceName);
        return new OpenMRSConceptBuilder().addConceptMapping(mapping).build();
    }

    private List<OpenMRSProvider> getProvidersData() {
        List<OpenMRSProvider> providers = new ArrayList<OpenMRSProvider>();
        providers.add(new OpenMRSProvider());
        return providers;
    }
}