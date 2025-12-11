package org.bahmni.module.pacsintegration.services.impl;

import org.bahmni.module.pacsintegration.atomfeed.contract.hl7.HL7CodedElement;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.ConceptMapType;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.ConceptReferenceTerm;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.ConceptSource;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderConcept;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderConceptMapping;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderConceptName;
import org.bahmni.module.pacsintegration.exception.ConceptCodeResolutionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class ConceptCodeResolverImplTest {

    @InjectMocks
    private ConceptCodeResolverImpl resolver;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(resolver, "sourcePriority", "LOINC,SNOMED-CT,PACS Procedure Code");
        ReflectionTestUtils.invokeMethod(resolver, "initializePrioritySources");
    }

    @Test
    public void shouldResolveConceptCodeFromFirstPrioritySource() {
        OrderConcept concept = buildConceptWithMapping("LOINC", "12345-6", "Glucose", "LN");
        HL7CodedElement result = resolver.resolveConceptCode(concept);

        assertNotNull(result);
        assertEquals("12345-6", result.getIdentifier());
        assertEquals("Glucose", result.getText());
        assertEquals("LN", result.getNameOfCodingSystem());
    }

    @Test
    public void shouldSkipFirstSourceAndUseSecondWhenFirstNotAvailable() {
        OrderConcept concept = buildConceptWithMapping("SNOMED-CT", "888888", "Test Procedure", "SCT");
        HL7CodedElement result = resolver.resolveConceptCode(concept);


        assertNotNull(result);
        assertEquals("888888", result.getIdentifier());
        assertEquals("Test Procedure", result.getText());
        assertEquals("SCT", result.getNameOfCodingSystem());
    }

    @Test
    public void shouldUseThirdPrioritySourceWhenFirstTwoNotAvailable() {
        OrderConcept concept = buildConceptWithMapping("PACS Procedure Code", "CT-001", "CT Scan", "PACS");
        HL7CodedElement result = resolver.resolveConceptCode(concept);


        assertNotNull(result);
        assertEquals("CT-001", result.getIdentifier());
        assertEquals("CT Scan", result.getText());
        assertEquals("PACS", result.getNameOfCodingSystem());
    }

    @Test
    public void shouldSkipNonSameAsTypeMappings() {
        ConceptSource source = new ConceptSource("uuid", "LOINC", "LN");
        ConceptReferenceTerm term = new ConceptReferenceTerm("Test", "12345", false, source);
        ConceptMapType mapType = new ConceptMapType("NARROWER-THAN");
        OrderConceptMapping mapping = new OrderConceptMapping(mapType, term);

        OrderConceptName conceptName = new OrderConceptName("uuid", "Test Concept", "Test Concept", "en", true, "FULLY_SPECIFIED");
        OrderConcept concept = new OrderConcept("test-uuid", Arrays.asList(conceptName), Arrays.asList(mapping));
        try {
            resolver.resolveConceptCode(concept);
            fail("Should throw ConceptCodeResolutionException");
        } catch (ConceptCodeResolutionException e) {
            assertTrue(e.getMessage().contains("No SAME-AS mapping found"));
        }
    }

    @Test
    public void shouldSkipMappingsWithNullCode() {

        ConceptSource source1 = new ConceptSource("uuid1", "LOINC", "LN");
        ConceptReferenceTerm term1 = new ConceptReferenceTerm("Test", null, false, source1);
        ConceptMapType mapType1 = new ConceptMapType("SAME-AS");
        OrderConceptMapping mapping1 = new OrderConceptMapping(mapType1, term1);

        ConceptSource source2 = new ConceptSource("uuid2", "LOINC", "LN");
        ConceptReferenceTerm term2 = new ConceptReferenceTerm("Valid Test", "12345", false, source2);
        ConceptMapType mapType2 = new ConceptMapType("SAME-AS");
        OrderConceptMapping mapping2 = new OrderConceptMapping(mapType2, term2);

        OrderConceptName conceptName = new OrderConceptName("uuid", "Test", "Test", "en", true, "FULLY_SPECIFIED");
        OrderConcept concept = new OrderConcept("test-uuid", Arrays.asList(conceptName), Arrays.asList(mapping1, mapping2));
        HL7CodedElement result = resolver.resolveConceptCode(concept);

        assertNotNull(result);
        assertEquals("12345", result.getIdentifier());
        assertEquals("Valid Test", result.getText());
    }

    @Test
    public void shouldSkipMappingsWithBlankCode() {

        ConceptSource source1 = new ConceptSource("uuid1", "LOINC", "LN");
        ConceptReferenceTerm term1 = new ConceptReferenceTerm("Test", "   ", false, source1);
        ConceptMapType mapType1 = new ConceptMapType("SAME-AS");
        OrderConceptMapping mapping1 = new OrderConceptMapping(mapType1, term1);

        ConceptSource source2 = new ConceptSource("uuid2", "LOINC", "LN");
        ConceptReferenceTerm term2 = new ConceptReferenceTerm("Valid Test", "67890", false, source2);
        ConceptMapType mapType2 = new ConceptMapType("SAME-AS");
        OrderConceptMapping mapping2 = new OrderConceptMapping(mapType2, term2);

        OrderConceptName conceptName = new OrderConceptName("uuid", "Test", "Test", "en", true, "FULLY_SPECIFIED");
        OrderConcept concept = new OrderConcept("test-uuid", Arrays.asList(conceptName), Arrays.asList(mapping1, mapping2));
        HL7CodedElement result = resolver.resolveConceptCode(concept);

        assertNotNull(result);
        assertEquals("67890", result.getIdentifier());
    }

    @Test
    public void shouldSkipRetiredConceptReferenceTerms() {
        ConceptSource source1 = new ConceptSource("uuid1", "LOINC", "LN");
        ConceptReferenceTerm term1 = new ConceptReferenceTerm("Retired Test", "11111", true, source1);
        ConceptMapType mapType1 = new ConceptMapType("SAME-AS");
        OrderConceptMapping mapping1 = new OrderConceptMapping(mapType1, term1);

        ConceptSource source2 = new ConceptSource("uuid2", "LOINC", "LN");
        ConceptReferenceTerm term2 = new ConceptReferenceTerm("Active Test", "22222", false, source2);
        ConceptMapType mapType2 = new ConceptMapType("SAME-AS");
        OrderConceptMapping mapping2 = new OrderConceptMapping(mapType2, term2);

        OrderConceptName conceptName = new OrderConceptName("uuid", "Test", "Test", "en", true, "FULLY_SPECIFIED");
        OrderConcept concept = new OrderConcept("test-uuid", Arrays.asList(conceptName), Arrays.asList(mapping1, mapping2));
        HL7CodedElement result = resolver.resolveConceptCode(concept);

        assertNotNull(result);
        assertEquals("22222", result.getIdentifier());
        assertEquals("Active Test", result.getText());
    }

    @Test
    public void shouldUseFirstMatchWhenMultipleSameAsMappingsExistForSameSource() {

        ConceptSource source = new ConceptSource("uuid", "LOINC", "LN");
        ConceptReferenceTerm term1 = new ConceptReferenceTerm("First Test", "FIRST-CODE", false, source);
        ConceptReferenceTerm term2 = new ConceptReferenceTerm("Second Test", "SECOND-CODE", false, source);

        ConceptMapType mapType = new ConceptMapType("SAME-AS");
        OrderConceptMapping mapping1 = new OrderConceptMapping(mapType, term1);
        OrderConceptMapping mapping2 = new OrderConceptMapping(mapType, term2);

        OrderConceptName conceptName = new OrderConceptName("uuid", "Test", "Test", "en", true, "FULLY_SPECIFIED");
        OrderConcept concept = new OrderConcept("test-uuid", Arrays.asList(conceptName), Arrays.asList(mapping1, mapping2));
        HL7CodedElement result = resolver.resolveConceptCode(concept);

        assertEquals("FIRST-CODE", result.getIdentifier());
        assertEquals("First Test", result.getText());
    }

    @Test
    public void shouldUseConceptReferenceTermNameWhenPresent() {
        // Given: Mapping with term name present
        OrderConcept concept = buildConceptWithMapping("LOINC", "12345", "Term Name Here", "LN");
        HL7CodedElement result = resolver.resolveConceptCode(concept);

        assertEquals("Term Name Here", result.getText());
    }

    @Test
    public void shouldFallbackToFullySpecifiedEnglishNameWhenTermNameMissing() {
        // Given: Mapping without term name, but concept has fully specified name
        ConceptSource source = new ConceptSource("uuid", "LOINC", "LN");
        ConceptReferenceTerm term = new ConceptReferenceTerm(null, "12345", false, source);
        ConceptMapType mapType = new ConceptMapType("SAME-AS");
        OrderConceptMapping mapping = new OrderConceptMapping(mapType, term);

        OrderConceptName fullySpecifiedName = new OrderConceptName("uuid1", "Fully Specified Name", "Fully Specified Name", "en", true, "FULLY_SPECIFIED");
        OrderConceptName shortName = new OrderConceptName("uuid2", "Short Name", "Short Name", "en", false, "SHORT");
        OrderConcept concept = new OrderConcept("test-uuid", Arrays.asList(fullySpecifiedName, shortName), Arrays.asList(mapping));
        HL7CodedElement result = resolver.resolveConceptCode(concept);

        assertEquals("Fully Specified Name", result.getText());
    }

    @Test
    public void shouldFallbackToAnyEnglishNameWhenFullySpecifiedMissing() {
        // Given: No term name, no fully specified English name, but has English name
        ConceptSource source = new ConceptSource("uuid", "LOINC", "LN");
        ConceptReferenceTerm term = new ConceptReferenceTerm(null, "12345", false, source);
        ConceptMapType mapType = new ConceptMapType("SAME-AS");
        OrderConceptMapping mapping = new OrderConceptMapping(mapType, term);

        OrderConceptName shortName = new OrderConceptName("uuid1", "English Short Name", "English Short Name", "en", false, "SHORT");
        OrderConceptName frenchName = new OrderConceptName("uuid2", "Nom Français", "Nom Français", "fr", false, "FULLY_SPECIFIED");
        OrderConcept concept = new OrderConcept("test-uuid", Arrays.asList(frenchName, shortName), Arrays.asList(mapping));
        HL7CodedElement result = resolver.resolveConceptCode(concept);

        assertEquals("English Short Name", result.getText());
    }

    @Test
    public void shouldFallbackToFirstNameWhenNoEnglishName() {
        // Given: No term name, no English names at all
        ConceptSource source = new ConceptSource("uuid", "LOINC", "LN");
        ConceptReferenceTerm term = new ConceptReferenceTerm(null, "12345", false, source);
        ConceptMapType mapType = new ConceptMapType("SAME-AS");
        OrderConceptMapping mapping = new OrderConceptMapping(mapType, term);

        OrderConceptName frenchName = new OrderConceptName("uuid1", "Nom Français", "Nom Français", "fr", true, "FULLY_SPECIFIED");
        OrderConceptName spanishName = new OrderConceptName("uuid2", "Nombre Español", "Nombre Español", "es", false, "SHORT");
        OrderConcept concept = new OrderConcept("test-uuid", Arrays.asList(frenchName, spanishName), Arrays.asList(mapping));
        HL7CodedElement result = resolver.resolveConceptCode(concept);

        assertEquals("Nom Français", result.getText());
    }

    @Test
    public void shouldUseHl7CodeWhenPresent() {
        // Given: Source has hl7Code
        OrderConcept concept = buildConceptWithMapping("LOINC", "12345", "Test", "LN");
        HL7CodedElement result = resolver.resolveConceptCode(concept);

        assertEquals("LN", result.getNameOfCodingSystem());
    }

    @Test
    public void shouldFallbackToSourceNameWhenHl7CodeMissing() {
        // Given: Source has no hl7Code but has name
        ConceptSource source = new ConceptSource("uuid", "LOINC", null);
        ConceptReferenceTerm term = new ConceptReferenceTerm("Test", "12345", false, source);
        ConceptMapType mapType = new ConceptMapType("SAME-AS");
        OrderConceptMapping mapping = new OrderConceptMapping(mapType, term);

        OrderConceptName conceptName = new OrderConceptName("uuid", "Test", "Test", "en", true, "FULLY_SPECIFIED");
        OrderConcept concept = new OrderConcept("test-uuid", Arrays.asList(conceptName), Arrays.asList(mapping));
        HL7CodedElement result = resolver.resolveConceptCode(concept);

        assertEquals("LOINC", result.getNameOfCodingSystem());
    }

    @Test(expected = ConceptCodeResolutionException.class)
    public void shouldThrowExceptionWhenConceptIsNull() {
        resolver.resolveConceptCode(null);
    }

    @Test(expected = ConceptCodeResolutionException.class)
    public void shouldThrowExceptionWhenMappingsListIsNull() {
        OrderConcept concept = new OrderConcept();
        concept.setUuid("test-uuid");
        concept.setMappings(null);
        resolver.resolveConceptCode(concept);
    }

    @Test(expected = ConceptCodeResolutionException.class)
    public void shouldThrowExceptionWhenMappingsListIsEmpty() {
        OrderConcept concept = new OrderConcept();
        concept.setUuid("test-uuid");
        concept.setMappings(Collections.emptyList());
        resolver.resolveConceptCode(concept);
    }

    @Test
    public void shouldThrowExceptionWhenNoSameAsMappingFoundInPrioritySources() {
        // Given: Concept has mapping but not in priority sources
        ConceptSource source = new ConceptSource("uuid", "ICD-10", "ICD10");
        ConceptReferenceTerm term = new ConceptReferenceTerm("Test", "A00.0", false, source);
        ConceptMapType mapType = new ConceptMapType("SAME-AS");
        OrderConceptMapping mapping = new OrderConceptMapping(mapType, term);

        OrderConceptName conceptName = new OrderConceptName("uuid", "Test", "Test", "en", true, "FULLY_SPECIFIED");
        OrderConcept concept = new OrderConcept("test-uuid", Arrays.asList(conceptName), Arrays.asList(mapping));
        try {
            resolver.resolveConceptCode(concept);
            fail("Should throw ConceptCodeResolutionException");
        } catch (ConceptCodeResolutionException e) {
            assertTrue(e.getMessage().contains("No SAME-AS mapping found"));
            assertTrue(e.getMessage().contains("test-uuid"));
        }
    }

    @Test(expected = ConceptCodeResolutionException.class)
    public void shouldThrowExceptionWhenConceptHasNoNames() {
        // Given: Concept with mapping but no names
        ConceptSource source = new ConceptSource("uuid", "LOINC", "LN");
        ConceptReferenceTerm term = new ConceptReferenceTerm(null, "12345", false, source);
        ConceptMapType mapType = new ConceptMapType("SAME-AS");
        OrderConceptMapping mapping = new OrderConceptMapping(mapType, term);

        OrderConcept concept = new OrderConcept("test-uuid", null, Arrays.asList(mapping));
        resolver.resolveConceptCode(concept);
    }

    @Test
    public void shouldHandleWhitespaceInPropertyValue() {
        // Given: Property with whitespace
        ReflectionTestUtils.setField(resolver, "sourcePriority", " LOINC , SNOMED-CT , PACS Procedure Code ");
        ReflectionTestUtils.invokeMethod(resolver, "initializePrioritySources");

        OrderConcept concept = buildConceptWithMapping("SNOMED-CT", "12345", "Test", "SCT");
        HL7CodedElement result = resolver.resolveConceptCode(concept);

        assertNotNull(result);
        assertEquals("12345", result.getIdentifier());
    }

    @Test
    public void shouldFilterOutEmptyStringsInPropertyValue() {
        // Given: Property with empty values
        ReflectionTestUtils.setField(resolver, "sourcePriority", "LOINC,,SNOMED-CT");
        ReflectionTestUtils.invokeMethod(resolver, "initializePrioritySources");

        OrderConcept concept = buildConceptWithMapping("SNOMED-CT", "12345", "Test", "SCT");
        HL7CodedElement result = resolver.resolveConceptCode(concept);

        assertNotNull(result);
        assertEquals("12345", result.getIdentifier());
    }

    @Test
    public void shouldSkipMappingWithNullConceptMapType() {
        // Given: First mapping has null conceptMapType, second is valid
        OrderConceptMapping mapping1 = new OrderConceptMapping(null, null);

        ConceptSource source = new ConceptSource("uuid", "LOINC", "LN");
        ConceptReferenceTerm term = new ConceptReferenceTerm("Test", "12345", false, source);
        ConceptMapType mapType = new ConceptMapType("SAME-AS");
        OrderConceptMapping mapping2 = new OrderConceptMapping(mapType, term);

        OrderConceptName conceptName = new OrderConceptName("uuid", "Test", "Test", "en", true, "FULLY_SPECIFIED");
        OrderConcept concept = new OrderConcept("test-uuid", Arrays.asList(conceptName), Arrays.asList(mapping1, mapping2));
        HL7CodedElement result = resolver.resolveConceptCode(concept);

        assertNotNull(result);
        assertEquals("12345", result.getIdentifier());
    }

    @Test
    public void shouldSkipMappingWithNullConceptMapTypeDisplay() {
        // Given: First mapping has null display, second is valid
        ConceptMapType mapType1 = new ConceptMapType(null);
        OrderConceptMapping mapping1 = new OrderConceptMapping(mapType1, null);

        ConceptSource source = new ConceptSource("uuid", "LOINC", "LN");
        ConceptReferenceTerm term = new ConceptReferenceTerm("Test", "12345", false, source);
        ConceptMapType mapType2 = new ConceptMapType("SAME-AS");
        OrderConceptMapping mapping2 = new OrderConceptMapping(mapType2, term);

        OrderConceptName conceptName = new OrderConceptName("uuid", "Test", "Test", "en", true, "FULLY_SPECIFIED");
        OrderConcept concept = new OrderConcept("test-uuid", Arrays.asList(conceptName), Arrays.asList(mapping1, mapping2));
        HL7CodedElement result = resolver.resolveConceptCode(concept);

        assertNotNull(result);
        assertEquals("12345", result.getIdentifier());
    }

    private OrderConcept buildConceptWithMapping(String sourceName, String code, String termName, String hl7Code) {
        ConceptSource source = new ConceptSource("source-uuid", sourceName, hl7Code);
        ConceptReferenceTerm term = new ConceptReferenceTerm(termName, code, false, source);
        ConceptMapType mapType = new ConceptMapType("SAME-AS");
        OrderConceptMapping mapping = new OrderConceptMapping(mapType, term);

        OrderConceptName conceptName = new OrderConceptName("name-uuid", "Concept Display Name", "Concept Display Name", "en", true, "FULLY_SPECIFIED");

        return new OrderConcept("concept-uuid", Arrays.asList(conceptName), Arrays.asList(mapping));
    }
}
