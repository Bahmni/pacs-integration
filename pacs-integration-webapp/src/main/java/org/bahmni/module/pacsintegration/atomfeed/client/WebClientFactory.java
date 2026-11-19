/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.pacsintegration.atomfeed.client;

import org.bahmni.webclients.Authenticator;
import org.bahmni.webclients.ConnectionDetails;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.WebClientsException;
import org.bahmni.webclients.OAuth2ConnectionDetails;
import org.bahmni.webclients.openmrs.OpenMRSLoginAuthenticator;
import org.bahmni.webclients.openmrs.OpenMRSOAuthLoginAuthenticator;

import java.util.Properties;

public class WebClientFactory {

    public static HttpClient getClient() {
        ConnectionDetails connectionDetails = org.bahmni.module.pacsintegration.atomfeed.client.ConnectionDetails.get();
        return new HttpClient(connectionDetails, getAuthenticator(connectionDetails));
    }


    private static Authenticator getAuthenticator(ConnectionDetails connectionDetails) {
        Properties properties = AtomFeedProperties.getInstance().getProperties();
        if (!isOAuth2Enabled(properties)) {
            return new OpenMRSLoginAuthenticator(connectionDetails);
        }
        return createOAuth2Authenticator(connectionDetails, properties);
    }

    private static boolean isOAuth2Enabled(Properties properties) {
        return "true".equalsIgnoreCase(trimmedOrNull(properties, "oauth2.enabled"));
    }

    private static Authenticator createOAuth2Authenticator(ConnectionDetails connectionDetails, Properties properties) {
        String tokenUrl = trimmedOrNull(properties, "openmrs.oauth.token.url");
        String clientId = trimmedOrNull(properties, "openmrs.oauth.client.id");
        String clientSecret = trimmedOrNull(properties, "openmrs.oauth.client.secret");
        validateOAuth2Settings(tokenUrl, clientId, clientSecret);
        OAuth2ConnectionDetails oAuth2ConnectionDetails = new OAuth2ConnectionDetails(connectionDetails.getAuthUrl(), tokenUrl,
                clientId, clientSecret, null, connectionDetails.getConnectionTimeout(),
                connectionDetails.getReadTimeout());
        return new OpenMRSOAuthLoginAuthenticator(oAuth2ConnectionDetails);
    }

    private static void validateOAuth2Settings(String tokenUrl, String clientId, String clientSecret) {
        if (tokenUrl == null || clientId == null || clientSecret == null) {
            throw new WebClientsException("oauth2.enabled is true but openmrs.oauth.token.url, openmrs.oauth.client.id "
                    + "and openmrs.oauth.client.secret must all be set");
        }
    }

    private static String trimmedOrNull(Properties properties, String key) {
        String value = properties.getProperty(key);
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
