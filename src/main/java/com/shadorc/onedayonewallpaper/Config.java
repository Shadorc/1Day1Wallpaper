package com.shadorc.onedayonewallpaper;

import reactor.util.Logger;
import reactor.util.Loggers;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class Config {

    private static final Logger LOGGER = Loggers.getLogger(Config.class);

    private static final Properties PROPERTIES = Config.getProperties();

    public static final int POST_HOUR = Integer.parseInt(PROPERTIES.getProperty("post.hour"));
    public static final Duration DEFAULT_TIMEOUT = Duration.ofMillis(Long.parseLong(PROPERTIES.getProperty("default.timeout")));

    private static Properties getProperties() {
        final Properties properties = new Properties();
        try (final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("project.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (final IOException err) {
            LOGGER.error("An error occurred while loading configuration file. Exiting.", err);
            System.exit(1);
        }
        return properties;
    }

}
