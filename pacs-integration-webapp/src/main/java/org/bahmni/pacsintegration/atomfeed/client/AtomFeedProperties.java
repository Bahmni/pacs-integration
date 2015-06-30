package org.bahmni.pacsintegration.atomfeed.client;

import java.io.InputStream;
import java.util.Properties;

public class AtomFeedProperties {


    private static final String FEED_CONNECT_TIMEOUT = "feed.connectionTimeoutInMilliseconds";
    private static final String FEED_REPLY_TIMEOUT = "feed.replyTimeoutInMilliseconds";
    private static final String FEED_MAX_FAILED_EVENTS = "feed.maxFailedEvents";
    private static final String FAILED_EVENT_MAX_RETRY = "feed.failedEventMaxRetry";

    public static final String DEFAULT_PROPERTY_FILENAME = "/atomfeed.properties";

    private Properties properties;

    private static AtomFeedProperties atomFeedProperties;

    private AtomFeedProperties() {
        InputStream propertyStream = null;
        try {
            propertyStream = this.getClass().getResourceAsStream(DEFAULT_PROPERTY_FILENAME);
            properties = new Properties();
            properties.load(propertyStream);

        } catch (Exception e) {
//            LogEvent.logError("AtomFeedProperties", "Constructor", e.toString());
        } finally {
            if (null != propertyStream) {
                try {
                    propertyStream.close();
                    propertyStream = null;
                } catch (Exception e) {
//                    LogEvent.logError("AtomFeedProperties", "Constructor final", e.toString());
                }
            }

        }
    }

    public static AtomFeedProperties getInstance() {
        if (atomFeedProperties == null) {
            synchronized (AtomFeedProperties.class) {
                if (atomFeedProperties == null) {
                    atomFeedProperties = new AtomFeedProperties();
                }
            }
        }
        return atomFeedProperties;
    }


    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    public String getFeedConnectionTimeout() {
        return getProperty(FEED_CONNECT_TIMEOUT);
    }

    public String getFeedReplyTimeout() {
        return getProperty(FEED_REPLY_TIMEOUT);
    }

    public String getMaxFailedEvents() {
        return getProperty(FEED_MAX_FAILED_EVENTS);
    }

    public String getFailedEventMaxRetry() {
        return getProperty(FAILED_EVENT_MAX_RETRY);
    }

}
