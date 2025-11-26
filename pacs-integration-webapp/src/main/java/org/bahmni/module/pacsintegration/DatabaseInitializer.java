package org.bahmni.module.pacsintegration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final JdbcTemplate jdbcTemplate;

    @Value("${openmrs.host}")
    private String openmrsHost;

    @Value("${openmrs.port}")
    private String openmrsPort;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Application started. Checking if database tables need initialization...");

        String newUrlPrefix = String.format("http://%s:%s/openmrs", openmrsHost, openmrsPort);

        try {
            // Update markers table
            String updateMarkersSql = "UPDATE markers SET feed_uri_for_last_read_entry = regexp_replace(feed_uri_for_last_read_entry, 'http://.*/openmrs', ?), " +
                    "feed_uri = regexp_replace(feed_uri, 'http://.*/openmrs', ?) " +
                    "WHERE feed_uri ~ 'openmrs'";
            int markersUpdated = jdbcTemplate.update(updateMarkersSql, newUrlPrefix, newUrlPrefix);
            log.info("Updated {} rows in the 'markers' table.", markersUpdated);

            // Update failed_events table
            String updateFailedEventsSql = "UPDATE failed_events SET feed_uri = regexp_replace(feed_uri, 'http://.*/openmrs', ?) " +
                    "WHERE feed_uri ~ 'openmrs'";
            int failedEventsUpdated = jdbcTemplate.update(updateFailedEventsSql, newUrlPrefix);
            log.info("Updated {} rows in the 'failed_events' table.", failedEventsUpdated);

        } catch (Exception e) {
            // This will catch errors like "table not found" on the very first run, which is expected and safe to ignore.
            log.warn("Could not run initial data update. This is normal on a fresh database. Error: {}", e.getMessage());
        }
    }
}