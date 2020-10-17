package com.shadorc.onedayonewallpaper;

import reactor.util.Logger;
import reactor.util.Loggers;
import twitter4j.JSONArray;
import twitter4j.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class Storage {

    private static final Logger LOGGER = Loggers.getLogger(Storage.class);

    private static Storage instance;

    static {
        Storage.instance = new Storage();
    }

    private final File historyFile;
    private final File imageFile;

    private Storage() {
        final File saveDir = new File("./saves");
        this.historyFile = new File(saveDir, "history.json");
        this.imageFile = new File(saveDir, "image.jpg");

        if (!saveDir.exists() && !saveDir.mkdir()) {
            throw new RuntimeException(String.format("%s could not be created.", saveDir.getName()));
        }

        if (!this.historyFile.exists()) {
            try {
                if (!this.historyFile.createNewFile()) {
                    throw new IOException(String.format("%s could not be created.", this.historyFile.getName()));
                }
                try (final FileWriter writer = new FileWriter(this.historyFile)) {
                    writer.write(new JSONArray().toString());
                }
            } catch (final IOException err) {
                LOGGER.error("An error occurred during the initialization of the data file. Exiting.", err);
                System.exit(1);
            }
        }
    }

    public File getImageFile() {
        return this.imageFile;
    }

    public void addToHistory(final String id) {
        final JSONArray jsonArray = this.getHistory();
        jsonArray.put(id);

        try (final FileWriter writer = new FileWriter(this.historyFile)) {
            writer.write(jsonArray.toString(2));
        } catch (final IOException err) {
            LOGGER.error("An error occurred while saving history.", err);
        }
    }

    public JSONArray getHistory() {
        JSONArray jsonArray = new JSONArray();
        try (final FileReader reader = new FileReader(this.historyFile)) {
            jsonArray = new JSONArray(new JSONTokener(reader));
        } catch (final IOException err) {
            LOGGER.error("An error occurred while getting history.", err);
        }
        return jsonArray;
    }

    public static Storage getInstance() {
        return Storage.instance;
    }

}