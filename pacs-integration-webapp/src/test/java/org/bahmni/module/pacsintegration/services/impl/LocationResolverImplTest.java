package org.bahmni.module.pacsintegration.services.impl;

import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderLocationInfo;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.*;
import org.bahmni.module.pacsintegration.exception.LocationResolutionException;
import org.bahmni.module.pacsintegration.services.OpenMRSService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LocationResolverImplTest {

    @InjectMocks
    private LocationResolverImpl resolver;

    @Mock
    private OpenMRSService openMRSService;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(resolver, "requestedLocationAttributeName", "REQUESTED_LOCATION");
    }

    @Test
    public void shouldUseFulfillingLocationFromRequestedLocationAttribute() throws IOException {
        OrderLocation requestedLocation = buildLocation("req-uuid", "Radiology Lab", "Radiology Lab");
        OrderLocation encounterLocation = buildLocationWithVisitTag("enc-uuid", "OPD", "OPD");

        Attribute attribute = buildAttribute("REQUESTED_LOCATION", requestedLocation);
        OpenMRSOrderDetails orderDetails = buildOrderDetails(encounterLocation, Arrays.asList(attribute));

        OrderLocationInfo result = resolver.resolveLocations(orderDetails);

        assertNotNull(result);
        assertEquals("req-uuid", result.getFulfillingLocation().getUuid());
        assertEquals("Radiology Lab", result.getFulfillingLocation().getName());
        assertEquals("enc-uuid", result.getSourceLocation().getUuid());

        verify(openMRSService, never()).getVisitLocation(anyString());
    }

    @Test
    public void shouldFallbackToSourceLocationWhenNoRequestedLocation() throws IOException {
        OrderLocation encounterLocation = buildLocationWithVisitTag("enc-uuid", "OPD", "OPD");
        OpenMRSOrderDetails orderDetails = buildOrderDetails(encounterLocation, Collections.emptyList());

        OrderLocationInfo result = resolver.resolveLocations(orderDetails);

        assertEquals("enc-uuid", result.getFulfillingLocation().getUuid());
        assertEquals("enc-uuid", result.getSourceLocation().getUuid());
        assertEquals("OPD", result.getFulfillingLocation().getName());
    }

    @Test
    public void shouldFallbackToSourceLocationWhenAttributesListIsNull() throws IOException {
        OrderLocation encounterLocation = buildLocationWithVisitTag("enc-uuid", "OPD", "OPD");
        OpenMRSOrderDetails orderDetails = buildOrderDetails(encounterLocation, null);

        OrderLocationInfo result = resolver.resolveLocations(orderDetails);

        assertEquals("enc-uuid", result.getFulfillingLocation().getUuid());
        assertEquals("enc-uuid", result.getSourceLocation().getUuid());
    }

    @Test
    public void shouldFallbackToSourceLocationWhenRequestedLocationValueIsNull() throws IOException {
        OrderLocation encounterLocation = buildLocationWithVisitTag("enc-uuid", "OPD", "OPD");
        Attribute attribute = buildAttribute("REQUESTED_LOCATION", null);
        OpenMRSOrderDetails orderDetails = buildOrderDetails(encounterLocation, Arrays.asList(attribute));

        OrderLocationInfo result = resolver.resolveLocations(orderDetails);

        assertEquals("enc-uuid", result.getFulfillingLocation().getUuid());
    }


    @Test
    public void shouldUseEncounterLocationAsSourceWhenTaggedAsVisitLocation() throws IOException {
        OrderLocation encounterLocation = buildLocationWithVisitTag("enc-uuid", "OPD", "OPD");
        OpenMRSOrderDetails orderDetails = buildOrderDetails(encounterLocation, null);

        OrderLocationInfo result = resolver.resolveLocations(orderDetails);

        assertEquals("enc-uuid", result.getSourceLocation().getUuid());
        assertEquals("OPD", result.getSourceLocation().getName());

        verify(openMRSService, never()).getVisitLocation(anyString());
        verify(openMRSService, never()).getLocation(anyString());
    }

    @Test
    public void shouldFetchVisitLocationFromAPIWhenNotTagged() throws IOException {
        OrderLocation encounterLocation = buildLocation("enc-uuid", "Room 101", "Room 101");
        OrderLocation parentVisitLocation = buildLocationWithVisitTag("parent-uuid", "Main Hospital", "Main Hospital");

        OpenMRSOrderDetails orderDetails = buildOrderDetails(encounterLocation, null);

        when(openMRSService.getVisitLocation("enc-uuid")).thenReturn("parent-uuid");
        when(openMRSService.getLocation("parent-uuid")).thenReturn(parentVisitLocation);

        OrderLocationInfo result = resolver.resolveLocations(orderDetails);

        assertEquals("parent-uuid", result.getSourceLocation().getUuid());
        assertEquals("Main Hospital", result.getSourceLocation().getName());

        verify(openMRSService, times(1)).getVisitLocation("enc-uuid");
        verify(openMRSService, times(1)).getLocation("parent-uuid");
    }

    @Test
    public void shouldFetchVisitLocationFromAPIWhenTagsIsNull() throws IOException {
        OrderLocation encounterLocation = buildLocation("enc-uuid", "Room 101", "Room 101");
        encounterLocation.setTags(null);

        OrderLocation parentVisitLocation = buildLocationWithVisitTag("parent-uuid", "Main Hospital", "Main Hospital");

        OpenMRSOrderDetails orderDetails = buildOrderDetails(encounterLocation, null);

        when(openMRSService.getVisitLocation("enc-uuid")).thenReturn("parent-uuid");
        when(openMRSService.getLocation("parent-uuid")).thenReturn(parentVisitLocation);

        OrderLocationInfo result = resolver.resolveLocations(orderDetails);

        assertEquals("parent-uuid", result.getSourceLocation().getUuid());
        verify(openMRSService, times(1)).getVisitLocation("enc-uuid");
        verify(openMRSService, times(1)).getLocation("parent-uuid");
    }

    @Test
    public void shouldFetchVisitLocationFromAPIWhenTagsIsEmpty() throws IOException {
        OrderLocation encounterLocation = buildLocation("enc-uuid", "Room 101", "Room 101");
        OrderLocation parentVisitLocation = buildLocationWithVisitTag("parent-uuid", "Main Hospital", "Main Hospital");

        OpenMRSOrderDetails orderDetails = buildOrderDetails(encounterLocation, null);

        when(openMRSService.getVisitLocation("enc-uuid")).thenReturn("parent-uuid");
        when(openMRSService.getLocation("parent-uuid")).thenReturn(parentVisitLocation);

        OrderLocationInfo result = resolver.resolveLocations(orderDetails);

        assertEquals("parent-uuid", result.getSourceLocation().getUuid());
        verify(openMRSService, times(1)).getVisitLocation("enc-uuid");
        verify(openMRSService, times(1)).getLocation("parent-uuid");
    }

    @Test(expected = LocationResolutionException.class)
    public void shouldThrowExceptionWhenGetVisitLocationAPIReturnsNull() throws IOException {
        OrderLocation encounterLocation = buildLocation("enc-uuid", "Room 101", "Room 101");
        OpenMRSOrderDetails orderDetails = buildOrderDetails(encounterLocation, null);

        when(openMRSService.getVisitLocation("enc-uuid")).thenReturn(null);
        resolver.resolveLocations(orderDetails);

        verify(openMRSService, never()).getLocation(anyString());
    }

    @Test
    public void shouldThrowExceptionWithCorrectMessageWhenVisitLocationNotFound() throws IOException {
        OrderLocation encounterLocation = buildLocation("enc-uuid", "Room 101", "Room 101");
        OpenMRSOrderDetails orderDetails = buildOrderDetails(encounterLocation, null);

        when(openMRSService.getVisitLocation("enc-uuid")).thenReturn(null);
        try {
            resolver.resolveLocations(orderDetails);
            fail("Should throw LocationResolutionException");
        } catch (LocationResolutionException e) {
            assertTrue(e.getMessage().contains("No visit location found"));
            assertTrue(e.getMessage().contains("enc-uuid"));
        }
    }

    @Test
    public void shouldThrowLocationResolutionExceptionWhenGetVisitLocationThrowsIOException() throws IOException {
        OrderLocation encounterLocation = buildLocation("enc-uuid", "Room 101", "Room 101");
        OpenMRSOrderDetails orderDetails = buildOrderDetails(encounterLocation, null);

        when(openMRSService.getVisitLocation("enc-uuid"))
                .thenThrow(new IOException("Network error"));
        try {
            resolver.resolveLocations(orderDetails);
            fail("Should throw LocationResolutionException");
        } catch (LocationResolutionException e) {
            assertTrue(e.getMessage().contains("Failed to fetch visit location"));
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof IOException);
        }
    }

    @Test
    public void shouldThrowLocationResolutionExceptionWhenGetLocationThrowsIOException() throws IOException {
        OrderLocation encounterLocation = buildLocation("enc-uuid", "Room 101", "Room 101");
        OpenMRSOrderDetails orderDetails = buildOrderDetails(encounterLocation, null);

        when(openMRSService.getVisitLocation("enc-uuid")).thenReturn("parent-uuid");
        when(openMRSService.getLocation("parent-uuid"))
                .thenThrow(new IOException("Network error"));
        try {
            resolver.resolveLocations(orderDetails);
            fail("Should throw LocationResolutionException");
        } catch (LocationResolutionException e) {
            assertTrue(e.getMessage().contains("Failed to fetch visit location"));
            assertNotNull(e.getCause());
        }
    }

    @Test(expected = LocationResolutionException.class)
    public void shouldThrowExceptionWhenOrderDetailsIsNull() {
        resolver.resolveLocations(null);
    }

    @Test
    public void shouldThrowExceptionWithMessageWhenEncounterIsNull() {
        OpenMRSOrderDetails orderDetails = new OpenMRSOrderDetails();
        orderDetails.setUuid("order-uuid");
        orderDetails.setEncounter(null);
        try {
            resolver.resolveLocations(orderDetails);
            fail("Should throw LocationResolutionException");
        } catch (LocationResolutionException e) {
            assertTrue(e.getMessage().contains("Encounter cannot be null"));
            assertTrue(e.getMessage().contains("order-uuid"));
        }
    }

    @Test
    public void shouldThrowExceptionWithMessageWhenEncounterLocationIsNull() {
        OpenMRSOrderDetails orderDetails = new OpenMRSOrderDetails();
        orderDetails.setUuid("order-uuid");
        OrderEncounter encounter = new OrderEncounter();
        encounter.setLocation(null);
        orderDetails.setEncounter(encounter);
        try {
            resolver.resolveLocations(orderDetails);
            fail("Should throw LocationResolutionException");
        } catch (LocationResolutionException e) {
            assertTrue(e.getMessage().contains("Encounter location cannot be null"));
            assertTrue(e.getMessage().contains("order-uuid"));
        }
    }


    private OrderLocation buildLocation(String uuid, String name, String display) {
        OrderLocation location = new OrderLocation();
        location.setUuid(uuid);
        location.setName(name);
        location.setDisplay(display);
        location.setTags(Collections.emptyList());
        return location;
    }

    private OrderLocation buildLocationWithVisitTag(String uuid, String name, String display) {
        LocationTag visitTag = new LocationTag();
        visitTag.setDisplay("Visit Location");

        OrderLocation location = new OrderLocation();
        location.setUuid(uuid);
        location.setName(name);
        location.setDisplay(display);
        location.setTags(Arrays.asList(visitTag));
        return location;
    }

    private Attribute buildAttribute(String attributeName, Object value) {
        AttributeType attributeType = new AttributeType();
        attributeType.setName(attributeName);

        Attribute attribute = new Attribute();
        attribute.setAttributeType(attributeType);
        attribute.setValue(value);
        return attribute;
    }

    private OpenMRSOrderDetails buildOrderDetails(OrderLocation encounterLocation,
                                                   java.util.List<Attribute> attributes) {
        OrderEncounter encounter = new OrderEncounter();
        encounter.setUuid("encounter-uuid");
        encounter.setLocation(encounterLocation);

        OpenMRSOrderDetails orderDetails = new OpenMRSOrderDetails();
        orderDetails.setUuid("order-uuid");
        orderDetails.setEncounter(encounter);
        orderDetails.setAttributes(attributes);

        return orderDetails;
    }
}
