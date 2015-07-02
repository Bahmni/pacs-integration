package org.bahmni.module.pacsintegration.atomfeed.mappers;

import org.bahmni.module.pacsintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConceptName;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class OpenMRSEncounterMapperTest extends OpenMRSMapperBaseTest {

    @Test
    public void shouldMapJsonIntoOpenMRSEncounter() throws IOException {
        String json = deserialize("/sampleOpenMRSEncounter.json");
        OpenMRSEncounterMapper openMRSEncounterMapper = new OpenMRSEncounterMapper(getObjectMapperThatAllowsComments());
        OpenMRSEncounter openMRSEncounter = openMRSEncounterMapper.map(json);
        Assert.assertNotNull(openMRSEncounter);

        Assert.assertEquals("7820b07d-50e9-4fed-b991-c38692b3d4ec", openMRSEncounter.getEncounterUuid());
        Assert.assertEquals(3, openMRSEncounter.getTestOrders().size());

        Assert.assertEquals("105059a8-5226-4b1f-b512-0d3ae685287d", openMRSEncounter.getPatientUuid());

        checkOrder(openMRSEncounter.getTestOrders().get(0), "ac0819a9-11c1-4310-8f0a-feee71e5086b", "4e167df9-80f5-4caa-9a82-ec97908cbd59", "Anaemia Panel", true);
        checkOrder(openMRSEncounter.getTestOrders().get(1), "ce3eeba1-2176-48d0-9ff4-53b731121274", "8c47a469-1b8f-47d9-9363-b97b45e3e740", "Routine Blood", true);
        checkOrder(openMRSEncounter.getTestOrders().get(2), "3508dbea-6844-4174-8787-40ab809f2522", "15a9d9e5-edcb-4e64-85ad-551693aeed5c", "ESR", false);
    }

    private ObjectMapper getObjectMapperThatAllowsComments() {
        ObjectMapper mapper = ObjectMapperForTest.MAPPER;
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        return mapper;
    }

    private void checkOrder(OpenMRSOrder openMRSOrder, String expectedOrderUUID, String expectedTestOrPanelUUID, String testOrPanelName, boolean isPanel) {
        Assert.assertEquals(expectedOrderUUID, openMRSOrder.getUuid());

        OpenMRSConcept concept = openMRSOrder.getConcept();
        Assert.assertEquals(expectedTestOrPanelUUID, concept.getUuid());
        Assert.assertEquals(expectedTestOrPanelUUID, openMRSOrder.getUUID());

        OpenMRSConceptName conceptName = concept.getName();
        Assert.assertEquals(testOrPanelName, conceptName.getName());
    }


}