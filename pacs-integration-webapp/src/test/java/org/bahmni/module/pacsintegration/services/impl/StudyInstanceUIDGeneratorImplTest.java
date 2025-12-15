package org.bahmni.module.pacsintegration.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class StudyInstanceUIDGeneratorImplTest {

    @InjectMocks
    private StudyInstanceUIDGeneratorImpl studyInstanceUIDGenerator;

    private static final String DEFAULT_PREFIX = "1.2.826.0.1.3680043.8.498";

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(studyInstanceUIDGenerator, "studyInstanceUIDPrefix", DEFAULT_PREFIX);
    }

    @Test
    public void shouldGenerateStudyInstanceUIDWithPrefixandDateCreatedandOrderNumberHash() {
        String orderNumber = "ORD-123";
        Date dateCreated = new Date();

        String result = studyInstanceUIDGenerator.generateStudyInstanceUID(orderNumber, dateCreated);

        String expectedUid = DEFAULT_PREFIX + "." + dateCreated.getTime() + "." + Math.abs(orderNumber.hashCode());

        assertNotNull(result);
        assertEquals(expectedUid, result);
    }

    @Test
    public void shouldGenerateDifferentStudyInstanceUIDsForDifferentOrderNumbers() {
        String orderNumber1 = "ORD-123";
        Date dateCreatedOrder1 = new Date();
        String orderNumber2 = "ORD-456";
        Date dateCreatedOrder2 = new Date();
        String uid1 = studyInstanceUIDGenerator.generateStudyInstanceUID(orderNumber1, dateCreatedOrder1);
        String uid2 = studyInstanceUIDGenerator.generateStudyInstanceUID(orderNumber2, dateCreatedOrder2);
        assertNotSame(uid1, uid2);
    }

    @Test
    public void shouldGenerateSameStudyInstanceUIDForSameOrderNumber() {
        String orderNumber = "ORD-123";
        Date dateCreatedOrder1 = new Date();
        String uid1 = studyInstanceUIDGenerator.generateStudyInstanceUID(orderNumber, dateCreatedOrder1);
        String uid2 = studyInstanceUIDGenerator.generateStudyInstanceUID(orderNumber, dateCreatedOrder1);
        assertEquals(uid1, uid2);
    }
}