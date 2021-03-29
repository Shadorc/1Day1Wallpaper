package com.shadorc.onedayonewallpaper.data;

import reactor.util.Logger;
import reactor.util.Loggers;
import twitter4j.JSONArray;
import twitter4j.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Storage {

    private static final Logger LOGGER = Loggers.getLogger(Storage.class);

    private static final File SAVE_DIR = new File("./saves");
    private static final File HISTORY_FILE = new File(SAVE_DIR, "history.json");
    private static final File IMAGE_FILE = new File(SAVE_DIR, "image.jpg");

    static {
        if (!SAVE_DIR.exists() && !SAVE_DIR.mkdir()) {
            throw new RuntimeException(String.format("%s could not be created", SAVE_DIR.getName()));
        }

        if (!Storage.HISTORY_FILE.exists()) {
            try {
                if (!Storage.HISTORY_FILE.createNewFile()) {
                    throw new IOException(String.format("%s could not be created", Storage.HISTORY_FILE.getName()));
                }
                try (final FileWriter writer = new FileWriter(Storage.HISTORY_FILE, StandardCharsets.UTF_8)) {
                    writer.write(new JSONArray().toString());
                }
            } catch (final IOException err) {
                LOGGER.error("An error occurred during the initialization of the data file, exiting", err);
                System.exit(1);
            }
        }
    }

    public static File getImageFile() {
        return Storage.IMAGE_FILE;
    }

    public static void addToHistory(String id) {
        final JSONArray jsonArray = Storage.getHistory();
        jsonArray.put(id);

        try (final FileWriter writer = new FileWriter(Storage.HISTORY_FILE, StandardCharsets.UTF_8)) {
            writer.write(jsonArray.toString(2));
        } catch (final IOException err) {
            LOGGER.error("An error occurred while saving history", err);
        }
    }

    public static JSONArray getHistory() {
        JSONArray jsonArray = new JSONArray();
        try (final FileReader reader = new FileReader(HISTORY_FILE, StandardCharsets.UTF_8)) {
            jsonArray = new JSONArray(new JSONTokener(reader));
        } catch (final IOException err) {
            LOGGER.error("An error occurred while getting history", err);
        }
        return jsonArray;
    }

}