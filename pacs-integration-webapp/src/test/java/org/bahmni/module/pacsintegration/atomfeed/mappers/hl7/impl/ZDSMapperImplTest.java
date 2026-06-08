package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.util.Terser;
import org.bahmni.module.pacsintegration.integrationtest.HL7Utils;
import org.bahmni.module.pacsintegration.services.StudyInstanceUIDGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ZDSMapperImplTest {

    @Mock
    private StudyInstanceUIDGenerator studyInstanceUIDGenerator;

    private ZDSMapperImpl zdsMapper;

    @Before
    public void setUp() {
        zdsMapper = new ZDSMapperImpl(studyInstanceUIDGenerator);
    }

    @Test
    public void shouldMapStudyInstanceUIDToZDSSegment() throws HL7Exception {
        ORM_O01 message = HL7Utils.createORM_O01Message();
        String orderNumber = "ORD-12345";
        Date dateCreated = new Date();
        String expectedUID = "1.2.826.0.1.3680043.8.498.12345678.123";

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(orderNumber, dateCreated))
                .thenReturn(expectedUID);

        zdsMapper.mapStudyInstanceUID(message, orderNumber, dateCreated);

        Terser terser = new Terser(message);
        String actualUID = terser.get("ZDS-1");

        assertEquals(expectedUID, actualUID);
    }

    @Test
    public void shouldCallStudyInstanceUIDGeneratorWithCorrectParameters() throws HL7Exception {
        ORM_O01 message = HL7Utils.createORM_O01Message();
        String orderNumber = "ORD-12345";
        Date dateCreated = new Date();
        String studyUID = "1.2.826.0.1.3680043.8.498.12345678.123";

        when(studyInstanceUIDGenerator.generateStudyInstanceUID(orderNumber, dateCreated))
                .thenReturn(studyUID);

        zdsMapper.mapStudyInstanceUID(message, orderNumber, dateCreated);

        verify(studyInstanceUIDGenerator, times(1)).generateStudyInstanceUID(orderNumber, dateCreated);
    }
}
