package com.shadorc.onedayonewallpaper.data.credential;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public final class CredentialManager {

    private static CredentialManager instance;

    static {
        CredentialManager.instance = new CredentialManager();
    }

    private final Properties credentialsProperties;

    private CredentialManager() {
        this.credentialsProperties = new Properties();
        File credentialsFile = new File("credentials.properties");

        if (!credentialsFile.exists()) {
            throw new RuntimeException(String.format("%s file is missing.", credentialsFile.getName()));
        }

        try (final BufferedReader reader = Files.newBufferedReader(credentialsFile.toPath())) {
            this.credentialsProperties.load(reader);
        } catch (final IOException err) {
            throw new RuntimeException(String.format("An error occurred while loading %s file.",
                    credentialsFile.getName()), err);
        }
    }

    public String get(final Credential key) {
        return this.credentialsProperties.getProperty(key.toString());
    }

    public static CredentialManager getInstance() {
        return CredentialManager.instance;
    }

}
