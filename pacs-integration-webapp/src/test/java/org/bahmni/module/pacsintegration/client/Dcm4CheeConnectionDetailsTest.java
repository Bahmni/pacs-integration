package org.bahmni.module.pacsintegration.client;

import org.bahmni.webclients.ConnectionDetails;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class Dcm4CheeConnectionDetailsTest {

    private static final String TEST_BASE_URL = "http://localhost:8080/dcm4chee";
    private static final int TEST_CONNECT_TIMEOUT = 5000;
    private static final int TEST_READ_TIMEOUT = 30000;

    @Before
    public void setUp() throws Exception {
        setStaticField("dcm4cheeBaseUrl", TEST_BASE_URL);
        setStaticField("connectTimeoutInMilliseconds", TEST_CONNECT_TIMEOUT);
        setStaticField("readTimeoutInMilliseconds", TEST_READ_TIMEOUT);
    }

    @After
    public void tearDown() throws Exception {
        setStaticField("dcm4cheeBaseUrl", null);
        setStaticField("connectTimeoutInMilliseconds", 0);
        setStaticField("readTimeoutInMilliseconds", 0);
    }

    @Test
    public void shouldReturnConnectionDetailsWithCorrectAuthUrl() {
        ConnectionDetails connectionDetails = Dcm4CheeConnectionDetails.get();

        assertNotNull(connectionDetails);
        assertEquals(TEST_BASE_URL, connectionDetails.getAuthUrl());
    }

    @Test
    public void shouldReturnNewConnectionDetailsInstanceOnEachCall() {
        ConnectionDetails connectionDetails1 = Dcm4CheeConnectionDetails.get();
        ConnectionDetails connectionDetails2 = Dcm4CheeConnectionDetails.get();

        assertNotNull(connectionDetails1);
        assertNotNull(connectionDetails2);
        assertNotSame(connectionDetails1, connectionDetails2);
    }

    @Test
    public void shouldHandleNullBaseUrl() throws Exception {
        setStaticField("dcm4cheeBaseUrl", null);

        ConnectionDetails connectionDetails = Dcm4CheeConnectionDetails.get();

        assertNotNull(connectionDetails);
        assertNull(connectionDetails.getAuthUrl());
    }

    @Test
    public void shouldHandleDifferentBaseUrls() throws Exception {
        setStaticField("dcm4cheeBaseUrl", "http://server1.com");
        ConnectionDetails cd1 = Dcm4CheeConnectionDetails.get();

        setStaticField("dcm4cheeBaseUrl", "http://server2.com");
        ConnectionDetails cd2 = Dcm4CheeConnectionDetails.get();

        assertEquals("http://server1.com", cd1.getAuthUrl());
        assertEquals("http://server2.com", cd2.getAuthUrl());
    }

    private void setStaticField(String fieldName, Object value) throws Exception {
        Field field = Dcm4CheeConnectionDetails.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}
