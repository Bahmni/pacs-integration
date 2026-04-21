package org.bahmni.module.pacsintegration.client;

import org.bahmni.webclients.HttpClient;

public class HttpClientFactory {

    public static HttpClient getDcm4CheeClient() {
        return new HttpClient(Dcm4CheeConnectionDetails.get());
    }
}
