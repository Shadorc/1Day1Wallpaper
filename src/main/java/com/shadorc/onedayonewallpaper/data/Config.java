package com.shadorc.onedayonewallpaper.data;

import reactor.util.Logger;
import reactor.util.Loggers;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class Config {

    private static final Logger LOGGER = Loggers.getLogger(Config.class);

    private static final Properties PROPERTIES = Config.loadProperties();

    public static final int POST_HOUR = Integer.parseInt(PROPERTIES.getProperty("post.hour"));
    public static final Duration DEFAULT_TIMEOUT = Duration.ofMillis(Long.parseLong(PROPERTIES.getProperty("default.timeout")));
    public static final int RETRY_MAX = Integer.parseInt(PROPERTIES.getProperty("retry.max"));

    private static Properties loadProperties() {
        final Properties properties = new Properties();
        try (final InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("project.properties")) {
            if (inputStream == null) {
                throw new RuntimeException("Configuration file not found. Exiting.");
            }
            properties.load(inputStream);
        } catch (final IOException err) {
            LOGGER.error("An error occurred while loading configuration file. Exiting.", err);
            throw new RuntimeException(err);
        }
        return properties;
    }

}
