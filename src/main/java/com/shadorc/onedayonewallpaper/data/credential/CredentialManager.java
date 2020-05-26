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
    private final File credentialsFile;

    private CredentialManager() {
        this.credentialsProperties = new Properties();
        this.credentialsFile = new File("credentials.properties");

        if (!this.credentialsFile.exists()) {
            throw new RuntimeException(String.format("%s file is missing.", this.credentialsFile.getName()));
        }

        try (final BufferedReader reader = Files.newBufferedReader(this.credentialsFile.toPath())) {
            this.credentialsProperties.load(reader);
        } catch (final IOException err) {
            throw new RuntimeException(String.format("An error occurred while loading %s file.",
                    this.credentialsFile.getName()), err);
        }
    }

    public String get(final Credential key) {
        return this.credentialsProperties.getProperty(key.toString());
    }

    public static CredentialManager getInstance() {
        return CredentialManager.instance;
    }

}
