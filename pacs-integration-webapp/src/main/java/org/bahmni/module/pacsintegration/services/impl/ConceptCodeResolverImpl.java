package org.bahmni.module.pacsintegration.services.impl;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.pacsintegration.atomfeed.contract.hl7.HL7CodedElement;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.ConceptMapType;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.ConceptReferenceTerm;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.ConceptSource;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderConcept;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderConceptMapping;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderConceptName;
import org.bahmni.module.pacsintegration.exception.ConceptCodeResolutionException;
import org.bahmni.module.pacsintegration.services.ConceptCodeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConceptCodeResolverImpl implements ConceptCodeResolver {

    private static final Logger logger = LoggerFactory.getLogger(ConceptCodeResolverImpl.class);
    private static final String SAME_AS_MAP_TYPE = "SAME-AS";
    private static final String ENGLISH_LOCALE = "en";
    private static final String FULLY_SPECIFIED_TYPE = "FULLY_SPECIFIED";

    @Value("${concept.code.resolver.source.priority:PACS Procedure Code}")
    private String sourcePriority;

    private List<String> prioritySourceNames;

    @PostConstruct
    private void initializePrioritySources() {
        prioritySourceNames = Arrays.stream(sourcePriority.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        logger.info("ConceptCodeResolver initialized with priority sources: {}", prioritySourceNames);
    }

    @Override
    public HL7CodedElement resolveConceptCode(OrderConcept concept) {
        if (concept == null) {
            throw new ConceptCodeResolutionException("OrderConcept cannot be null");
        }
        if (concept.getMappings() == null || concept.getMappings().isEmpty()) {
            throw new ConceptCodeResolutionException(
                    "OrderConcept must have mappings. Concept UUID: " + concept.getUuid());
        }
        for (String sourceName : prioritySourceNames) {
            logger.debug("Searching for SAME-AS mapping in source: {}", sourceName);

            OrderConceptMapping mapping = findMappingBySameAsType(concept, sourceName);
            if (mapping != null) {
                return buildCodedElement(concept, mapping);
            }
        }
        throw new ConceptCodeResolutionException(
                "No SAME-AS mapping found in priority sources for concept UUID: " + concept.getUuid());
    }

    private OrderConceptMapping findMappingBySameAsType(OrderConcept concept, String sourceNameToMatch) {
        for (OrderConceptMapping mapping : concept.getMappings()) {

            if (mapping.getConceptMapType() == null) {
                continue;
            }
            if (!isSameAsMapType(mapping.getConceptMapType())) {
                continue;
            }
            ConceptReferenceTerm term = mapping.getConceptReferenceTerm();
            if (term == null) {
                continue;
            }
            if (StringUtils.isBlank(term.getCode())) {
                logger.debug("Skipping mapping with null/blank code for concept: {}", concept.getUuid());
                continue;
            }
            if (term.getRetired() != null && term.getRetired()) {
                logger.debug("Skipping retired mapping with code: {}", term.getCode());
                continue;
            }
            ConceptSource source = term.getConceptSource();
            if (source == null) {
                continue;
            }
            if (sourceNameToMatch.equals(source.getName())) {
                logger.debug("Found SAME-AS mapping - Source: {}, Code: {}", source.getName(), term.getCode());
                return mapping;
            }
        }

        return null;
    }

    private boolean isSameAsMapType(ConceptMapType mapType) {
        return mapType.getDisplay() != null && SAME_AS_MAP_TYPE.equals(mapType.getDisplay());
    }

    private HL7CodedElement buildCodedElement(OrderConcept concept, OrderConceptMapping mapping) {
        ConceptReferenceTerm term = mapping.getConceptReferenceTerm();

        String identifier = term.getCode();
        String text = extractCodeMeaning(concept, term);
        String nameOfCodingSystem = extractCodeSchemeDesignator(term);

        logger.info("Resolved concept code - UUID: {}, Code: {}, Source: {}",
                concept.getUuid(), identifier, nameOfCodingSystem);

        return new HL7CodedElement(identifier, text, nameOfCodingSystem);
    }

    private String extractCodeMeaning(OrderConcept concept, ConceptReferenceTerm term) {
        if (StringUtils.isNotBlank(term.getName())) {
            return term.getName();
        }

        return getEnglishName(concept);
    }

    private String getEnglishName(OrderConcept concept) {
        if (concept.getNames() == null || concept.getNames().isEmpty()) {
            throw new ConceptCodeResolutionException(
                    "No concept names found for concept UUID: " + concept.getUuid());
        }

        for (OrderConceptName name : concept.getNames()) {
            if (isFullySpecifiedEnglishName(name)) {
                return name.getName();
            }
        }

        for (OrderConceptName name : concept.getNames()) {
            if (isEnglishName(name)) {
                return name.getName();
            }
        }

        return concept.getNames().get(0).getName();
    }

    private boolean isFullySpecifiedEnglishName(OrderConceptName name) {
        return ENGLISH_LOCALE.equals(name.getLocale()) &&
                FULLY_SPECIFIED_TYPE.equals(name.getConceptNameType());
    }

    private boolean isEnglishName(OrderConceptName name) {
        return ENGLISH_LOCALE.equals(name.getLocale());
    }

    private String extractCodeSchemeDesignator(ConceptReferenceTerm term) {
        ConceptSource source = term.getConceptSource();
        if (source != null && StringUtils.isNotBlank(source.getHl7Code())) {
            return source.getHl7Code();
        }
        if (source != null && StringUtils.isNotBlank(source.getName())) {
            return source.getName();
        }
        throw new ConceptCodeResolutionException(
                "ConceptSource has neither hl7Code nor name for term code: " + term.getCode());
    }
}
