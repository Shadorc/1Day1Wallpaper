package com.shadorc.onedayonewallpaper.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class Credentials {

    private static final Properties CREDENTIALS_PROPERTIES = new Properties();
    private static final File CREDENTIALS_FILE = new File("credentials.properties");

    static {
        if (!CREDENTIALS_FILE.exists()) {
            throw new RuntimeException(String.format("%s file is missing.", CREDENTIALS_FILE.getName()));
        }

        try (final BufferedReader reader = Files.newBufferedReader(CREDENTIALS_FILE.toPath())) {
            CREDENTIALS_PROPERTIES.load(reader);
        } catch (final IOException err) {
            throw new RuntimeException(String.format("An error occurred while loading %s file.", CREDENTIALS_FILE.getName()), err);
        }
    }

    public static String get(final Credential key) {
        return CREDENTIALS_PROPERTIES.getProperty(key.toString());
    }


}
