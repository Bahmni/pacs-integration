package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import org.bahmni.module.pacsintegration.atomfeed.contract.hl7.HL7CodedElement;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderConcept;
import org.bahmni.module.pacsintegration.integrationtest.HL7Utils;
import org.bahmni.module.pacsintegration.services.ConceptCodeResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OBRMapperImplTest {

    @Mock
    private ConceptCodeResolver conceptCodeResolver;

    private OBRMapperImpl obrMapper;
    private OBR obr;
    private OpenMRSOrderDetails orderDetails;

    @Before
    public void setUp() throws HL7Exception {
        obrMapper = new OBRMapperImpl(conceptCodeResolver);
        ORM_O01 message = HL7Utils.createORM_O01Message();
        obr = message.getORDER().getORDER_DETAIL().getOBR();
        orderDetails = HL7Utils.createOrderDetailsWithProvider();
    }

    @Test
    public void shouldMapAllOBRFields() throws HL7Exception {
        HL7CodedElement codedElement = createHL7CodedElement();

        when(conceptCodeResolver.resolveConceptCode(any(OrderConcept.class))).thenReturn(codedElement);

        obrMapper.map(obr, orderDetails);

        assertEquals("12345", obr.getUniversalServiceIdentifier().getIdentifier().getValue());
        assertEquals("CT Scan", obr.getUniversalServiceIdentifier().getText().getValue());
        assertEquals("LOINC", obr.getUniversalServiceIdentifier().getNameOfCodingSystem().getValue());
        assertEquals("ORD-12345", obr.getPlacerField1().getValue());
        assertEquals("John", obr.getOrderingProvider(0).getGivenName().getValue());
        assertEquals("Doe", obr.getOrderingProvider(0).getFamilyName().getSurname().getValue());
        assertEquals("provider-uuid", obr.getOrderingProvider(0).getIDNumber().getValue());
    }

    @Test
    public void shouldMapProcedureCodeWithPrimaryFields() throws HL7Exception {
        HL7CodedElement codedElement = createHL7CodedElement();

        when(conceptCodeResolver.resolveConceptCode(any(OrderConcept.class))).thenReturn(codedElement);

        obrMapper.map(obr, orderDetails);

        assertEquals("12345", obr.getUniversalServiceIdentifier().getIdentifier().getValue());
        assertEquals("CT Scan", obr.getUniversalServiceIdentifier().getText().getValue());
        assertEquals("LOINC", obr.getUniversalServiceIdentifier().getNameOfCodingSystem().getValue());
    }

    @Test
    public void shouldMapProcedureCodeWithAlternateFields() throws HL7Exception {
        HL7CodedElement codedElement = createHL7CodedElement();

        when(conceptCodeResolver.resolveConceptCode(any(OrderConcept.class))).thenReturn(codedElement);

        obrMapper.map(obr, orderDetails);

        assertEquals("12345", obr.getUniversalServiceIdentifier().getAlternateIdentifier().getValue());
        assertEquals("CT Scan", obr.getUniversalServiceIdentifier().getAlternateText().getValue());
        assertEquals("LOINC", obr.getUniversalServiceIdentifier().getNameOfAlternateCodingSystem().getValue());
    }

    @Test
    public void shouldMapAccessionNumber() throws HL7Exception {
        HL7CodedElement codedElement = createHL7CodedElement();

        when(conceptCodeResolver.resolveConceptCode(any(OrderConcept.class))).thenReturn(codedElement);

        obrMapper.map(obr, orderDetails);

        assertEquals("ORD-12345", obr.getPlacerField1().getValue());
    }

    @Test
    public void shouldMapOrderingProviderWithGivenName() throws HL7Exception {
        HL7CodedElement codedElement = createHL7CodedElement();

        when(conceptCodeResolver.resolveConceptCode(any(OrderConcept.class))).thenReturn(codedElement);

        obrMapper.map(obr, orderDetails);

        assertEquals("John", obr.getOrderingProvider(0).getGivenName().getValue());
    }

    @Test
    public void shouldMapOrderingProviderWithFamilyName() throws HL7Exception {
        HL7CodedElement codedElement = createHL7CodedElement();

        when(conceptCodeResolver.resolveConceptCode(any(OrderConcept.class))).thenReturn(codedElement);

        obrMapper.map(obr, orderDetails);

        assertEquals("Doe", obr.getOrderingProvider(0).getFamilyName().getSurname().getValue());
    }

    @Test
    public void shouldMapOrderingProviderWithUuid() throws HL7Exception {
        HL7CodedElement codedElement = createHL7CodedElement();

        when(conceptCodeResolver.resolveConceptCode(any(OrderConcept.class))).thenReturn(codedElement);

        obrMapper.map(obr, orderDetails);

        assertEquals("provider-uuid", obr.getOrderingProvider(0).getIDNumber().getValue());
    }

    @Test
    public void shouldCallConceptCodeResolverWithOrderConcept() throws HL7Exception {
        HL7CodedElement codedElement = createHL7CodedElement();
        OrderConcept concept = orderDetails.getConcept();

        when(conceptCodeResolver.resolveConceptCode(concept)).thenReturn(codedElement);

        obrMapper.map(obr, orderDetails);

        verify(conceptCodeResolver, times(1)).resolveConceptCode(concept);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionWhenHL7ExceptionOccurs() throws HL7Exception {

        when(conceptCodeResolver.resolveConceptCode(any(OrderConcept.class)))
                .thenThrow(new RuntimeException("Test exception"));

        obrMapper.map(obr, orderDetails);
    }

    @Test
    public void shouldPropagateRuntimeExceptionFromConceptCodeResolver() {

        when(conceptCodeResolver.resolveConceptCode(any(OrderConcept.class)))
                .thenThrow(new RuntimeException("Concept resolution failed"));

        try {
            obrMapper.map(obr, orderDetails);
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertNotNull(e.getMessage());
            assertEquals("Concept resolution failed", e.getMessage());
        }
    }

    private HL7CodedElement createHL7CodedElement() {
        return new HL7CodedElement("12345", "CT Scan", "LOINC");
    }
}
