package org.bahmni.module.pacsintegration.services;

import org.bahmni.module.pacsintegration.atomfeed.contract.hl7.HL7CodedElement;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderConcept;
import org.springframework.stereotype.Service;

@Service
public interface ConceptCodeResolver {
    HL7CodedElement resolveConceptCode(OrderConcept concept);
}
