package org.bahmni.module.pacsintegration.services;

import org.apache.commons.lang.StringUtils;
import org.bahmni.module.pacsintegration.client.HttpClientFactory;
import org.bahmni.module.pacsintegration.dto.DicomMetadataDTO;
import org.bahmni.webclients.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Dcm4CheeService {

    private static final Logger logger = LoggerFactory.getLogger(Dcm4CheeService.class);
    private static final String METADATA_URL_PATTERN = "%s/dcm4chee-arc/aets/%s/rs/studies/%s/metadata";

    @Value("${dcm4chee.base.url}")
    private String dcm4cheeBaseUrl;

    @Value("${dcm4chee.aet}")
    private String dcm4cheeAet;

    public DicomMetadataDTO[] fetchStudyMetadata(String studyInstanceUID) throws IOException {
        if (StringUtils.isBlank(dcm4cheeBaseUrl)) {
            logger.info("dcm4cheeBaseUrl is not configured, Skipping fetchStudyMetadata api call");
            return null;
        }
        HttpClient webClient = HttpClientFactory.getDcm4CheeClient();
        String url = buildMetadataUrl(studyInstanceUID);
        logger.debug("Calling DCM4CHEE metadata API: {}", url);
        return webClient.get(url, DicomMetadataDTO[].class);
    }

    private String buildMetadataUrl(String studyInstanceUID) {
        return String.format(METADATA_URL_PATTERN,
                dcm4cheeBaseUrl, dcm4cheeAet, studyInstanceUID);
    }
}
