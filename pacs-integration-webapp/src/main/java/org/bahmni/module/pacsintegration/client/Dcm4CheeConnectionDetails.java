package org.bahmni.module.pacsintegration.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class Dcm4CheeConnectionDetails {
    @Value("${dcm4chee.base.url}")
    private static String dcm4cheeBaseUrl;

    @Value("${dcm4chee.connection.timeout}")
    private static int connectTimeoutInMilliseconds;

    @Value("${dcm4chee.read.timeout}")
    private static int readTimeoutInMilliseconds;


    public static org.bahmni.webclients.ConnectionDetails get() {
        return new org.bahmni.webclients.ConnectionDetails(dcm4cheeBaseUrl, null, null, connectTimeoutInMilliseconds, readTimeoutInMilliseconds);
    }
}
