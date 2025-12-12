package org.bahmni.module.pacsintegration.services.impl;

import org.bahmni.module.pacsintegration.atomfeed.contract.order.LocationDTO;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderLocationInfo;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.*;
import org.bahmni.module.pacsintegration.exception.LocationResolutionException;
import org.bahmni.module.pacsintegration.services.LocationResolver;
import org.bahmni.module.pacsintegration.services.OpenMRSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LocationResolverImpl implements LocationResolver {

    private static final Logger logger = LoggerFactory.getLogger(LocationResolverImpl.class);

    @Value("${order.requested.location.attribute.name:REQUESTED_LOCATION}")
    private String requestedLocationAttributeName;


    private final String visitLocationTagName = "Visit Location";

    @Autowired
    private OpenMRSService openMRSService;

    @Override
    public OrderLocationInfo resolveLocations(OpenMRSOrderDetails orderDetails) {
        validateInput(orderDetails);

        LocationDTO sourceLocation = resolveSourceLocation(orderDetails);

        LocationDTO fulfillingLocation = resolveFulfillingLocation(orderDetails, sourceLocation);

        logger.info("Resolved locations for order {} - Fulfilling: {}, Source: {}",
                orderDetails.getUuid(), fulfillingLocation.getUuid(), sourceLocation.getUuid());

        return new OrderLocationInfo(fulfillingLocation, sourceLocation);
    }

    private void validateInput(OpenMRSOrderDetails orderDetails) {
        if (orderDetails == null) {
            throw new LocationResolutionException("OpenMRSOrderDetails cannot be null");
        }

        if (orderDetails.getEncounter() == null) {
            throw new LocationResolutionException(
                    "Encounter cannot be null for order: " + orderDetails.getUuid());
        }

        if (orderDetails.getEncounter().getLocation() == null) {
            throw new LocationResolutionException(
                    "Encounter location cannot be null for order: " + orderDetails.getUuid());
        }
    }

    private LocationDTO resolveFulfillingLocation(OpenMRSOrderDetails orderDetails, LocationDTO sourceLocation) {
        logger.debug("Resolving fulfilling location for order: {}", orderDetails.getUuid());
        OrderLocation requestedLocation = getRequestedLocationFromAttributes(orderDetails.getAttributes());

        if (requestedLocation != null) {
            logger.debug("Found REQUESTED_LOCATION attribute: {}", requestedLocation.getUuid());
            return buildLocationDTO(requestedLocation);
        }

        logger.debug("No REQUESTED_LOCATION found, using source location: {}", sourceLocation.getUuid());
        return sourceLocation;
    }

    private OrderLocation getRequestedLocationFromAttributes(java.util.List<Attribute> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            logger.debug("No attributes found in order");
            return null;
        }

        for (Attribute attribute : attributes) {
            if (attribute.getAttributeType() == null) {
                continue;
            }
            if (requestedLocationAttributeName.equals(attribute.getAttributeType().getName())) {
                Object value = attribute.getValue();

                if (value == null) {
                    logger.warn("REQUESTED_LOCATION attribute found but value is null");
                    continue;
                }
                if (value instanceof OrderLocation) {
                    return (OrderLocation) value;
                }

                logger.warn("REQUESTED_LOCATION attribute value is not of type OrderLocation: {}",
                        value.getClass().getName());
            }
        }

        return null;
    }

    private LocationDTO resolveSourceLocation(OpenMRSOrderDetails orderDetails) {
        logger.debug("Resolving source location (visit location) for order: {}", orderDetails.getUuid());

        OrderLocation encounterLocation = orderDetails.getEncounter().getLocation();

        if (isVisitLocation(encounterLocation)) {
            logger.debug("Encounter location {} is tagged as Visit Location", encounterLocation.getUuid());
            return buildLocationDTO(encounterLocation);
        }

        logger.debug("Encounter location not tagged as Visit Location, calling API for UUID: {}",
                encounterLocation.getUuid());

        return fetchVisitLocationFromAPI(encounterLocation.getUuid());
    }

    private boolean isVisitLocation(OrderLocation location) {
        if (location.getTags() == null || location.getTags().isEmpty()) {
            logger.debug("Location {} has no tags", location.getUuid());
            return false;
        }

        for (LocationTag tag : location.getTags()) {
            if (tag.getDisplay() != null && visitLocationTagName.equals(tag.getDisplay())) {
                return true;
            }
        }

        return false;
    }

    private LocationDTO fetchVisitLocationFromAPI(String locationUuid) {
        try {
            String visitLocationUuid = openMRSService.getVisitLocation(locationUuid);

            if (visitLocationUuid == null) {
                throw new LocationResolutionException(
                        "No visit location found in hierarchy for location UUID: " + locationUuid);
            }

            logger.debug("Fetched visit location UUID from API: {}", visitLocationUuid);

            OrderLocation visitLocation = openMRSService.getLocation(visitLocationUuid);

            logger.debug("Successfully fetched full visit location details from API: {}",
                    visitLocation.getUuid());

            return buildLocationDTO(visitLocation);

        } catch (IOException e) {
            throw new LocationResolutionException(
                    "Failed to fetch visit location from API for UUID: " + locationUuid, e);
        }
    }

    private LocationDTO buildLocationDTO(OrderLocation location) {
        return new LocationDTO(
                location.getUuid(),
                location.getName(),
                location.getDisplay()
        );
    }
}
