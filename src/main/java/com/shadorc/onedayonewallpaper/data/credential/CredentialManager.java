package com.shadorc.onedayonewallpaper.data.credential;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class CredentialManager {

    private static final Properties PROPERTIES = new Properties();

    static {
        final File credentialsFile = new File("credentials.properties");
        if (!credentialsFile.exists()) {
            throw new RuntimeException(String.format("%s file is missing", credentialsFile.getName()));
        }

        try (final BufferedReader reader = Files.newBufferedReader(credentialsFile.toPath())) {
            CredentialManager.PROPERTIES.load(reader);
        } catch (final IOException err) {
            throw new RuntimeException(String.format("An error occurred while loading %s file",
                    credentialsFile.getName()), err);
        }
    }

    public static String get(Credential key) {
        return CredentialManager.PROPERTIES.getProperty(key.toString());
    }

}
