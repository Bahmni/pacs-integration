package org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v25.datatype.XCN;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import org.bahmni.module.pacsintegration.atomfeed.contract.hl7.HL7CodedElement;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.Person;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.Provider;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.OBRMapper;
import org.bahmni.module.pacsintegration.model.OrderDetails;
import org.bahmni.module.pacsintegration.services.ConceptCodeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OBRMapperImpl implements OBRMapper {

    private static final Logger logger = LoggerFactory.getLogger(OBRMapperImpl.class);

    private final ConceptCodeResolver conceptCodeResolver;

    @Autowired
    public OBRMapperImpl(ConceptCodeResolver conceptCodeResolver) {
        this.conceptCodeResolver = conceptCodeResolver;
    }

    @Override
    public void map(OBR obr, OpenMRSOrderDetails orderDetails) {
        try {
            logger.debug("Mapping OBR segment for order ID: {}", orderDetails.getOrderNumber());
            mapProcedureCode(obr, orderDetails);
            mapAccessionNumber(obr, orderDetails);
            mapOrderingProvider(obr, orderDetails);
            logger.info("Successfully mapped OBR segment for order ID: {}", orderDetails.getOrderNumber());

        } catch (HL7Exception e) {
            logger.error("Error mapping OBR segment for order ID: {}", orderDetails.getOrderNumber(), e);
            throw new RuntimeException("Failed to map observation request segment", e);
        }
    }

    private void mapProcedureCode(OBR obr, OpenMRSOrderDetails orderDetails) throws DataTypeException {
        HL7CodedElement code = conceptCodeResolver.resolveConceptCode(orderDetails.getConcept());

        CE universalServiceIdentifier = obr.getUniversalServiceIdentifier();
        universalServiceIdentifier.getIdentifier().setValue(code.getIdentifier());
        universalServiceIdentifier.getText().setValue(code.getText());
        universalServiceIdentifier.getNameOfCodingSystem().setValue(code.getNameOfCodingSystem());
        universalServiceIdentifier.getAlternateIdentifier().setValue(code.getIdentifier());
        universalServiceIdentifier.getAlternateText().setValue(code.getText());
        universalServiceIdentifier.getNameOfAlternateCodingSystem().setValue(code.getNameOfCodingSystem());
    }

    private void mapAccessionNumber(OBR obr, OpenMRSOrderDetails orderDetails) throws HL7Exception {
        obr.getPlacerField1().parse(orderDetails.getOrderNumber());
    }

    private void mapOrderingProvider(OBR obr, OpenMRSOrderDetails orderDetails) throws HL7Exception {
        Provider creator = orderDetails.getCreator();
        Person creatorPerson = creator.getPerson();
        XCN orderingProvider = obr.getOrderingProvider(0);

        orderingProvider.getGivenName().setValue(creatorPerson.getPreferredName().getGivenName());
        orderingProvider.getFamilyName().getSurname().setValue(creatorPerson.getPreferredName().getFamilyName());
        orderingProvider.getIDNumber().setValue(creator.getUuid());
    }
}
