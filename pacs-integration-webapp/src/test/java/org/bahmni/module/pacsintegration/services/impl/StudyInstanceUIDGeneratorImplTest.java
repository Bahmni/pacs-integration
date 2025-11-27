package org.bahmni.module.pacsintegration.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

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
    public void shouldGenerateStudyInstanceUIDWithPrefixandOrderNumberHash() {
        String orderNumber = "ORD-123";

        String result = studyInstanceUIDGenerator.generateStudyInstanceUID(orderNumber);

        String expectedUid = DEFAULT_PREFIX + "." + Math.abs(orderNumber.hashCode());

        assertNotNull(result);
        assertEquals(expectedUid, result);
    }
}