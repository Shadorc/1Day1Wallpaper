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

    private static final File SAVE_DIR = new File("./saves");
    private static final File HISTORY_FILE = new File(SAVE_DIR, "history.json");
    public static final File IMAGE_FILE = new File(SAVE_DIR, "image.jpg");

    static {
        if (!SAVE_DIR.exists() && !SAVE_DIR.mkdir()) {
            throw new RuntimeException(String.format("%s could not be created.", SAVE_DIR.getName()));
        }

        if (!HISTORY_FILE.exists()) {
            try {
                if (!HISTORY_FILE.createNewFile()) {
                    throw new IOException(String.format("%s could not be created.", HISTORY_FILE.getName()));
                }
                try (final FileWriter writer = new FileWriter(HISTORY_FILE)) {
                    writer.write(new JSONArray().toString());
                }
            } catch (final IOException err) {
                LOGGER.error("An error occurred during the initialization of the data file. Exiting.", err);
                System.exit(1);
            }
        }
    }

    public static void addToHistory(final String id) {
        final JSONArray jsonArray = Storage.getHistory();
        jsonArray.put(id);

        try (final FileWriter writer = new FileWriter(HISTORY_FILE)) {
            writer.write(jsonArray.toString(2));
        } catch (final IOException err) {
            LOGGER.error("An error occurred while saving history.", err);
        }
    }

    public static JSONArray getHistory() {
        JSONArray jsonArray = new JSONArray();
        try (final FileReader reader = new FileReader(HISTORY_FILE)) {
            jsonArray = new JSONArray(new JSONTokener(reader));
        } catch (final IOException err) {
            LOGGER.error("An error occurred while getting history.", err);
        }
        return jsonArray;
    }

}