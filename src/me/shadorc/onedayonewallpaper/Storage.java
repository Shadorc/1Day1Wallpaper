package me.shadorc.onedayonewallpaper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

public class Storage {

	private static final Properties PROPERTIES = new Properties();

	private static final File CONF_FILE = new File("config.properties");
	private static final File HISTORY_FILE = new File("history.json");

	public static final File IMAGE_FILE = new File("image.jpg");

	static {
		if(!HISTORY_FILE.exists()) {
			try (FileWriter writer = new FileWriter(HISTORY_FILE)) {
				HISTORY_FILE.createNewFile();
				writer.write(new JSONArray().toString());
			} catch (IOException e) {
				Config.LOGGER.error("An error occurred during the initialisation of the data file. Exiting.", e);
				System.exit(1);
			}
		}

		if(!CONF_FILE.exists()) {
			Config.LOGGER.error("Properties file is missing. Exiting.");
			System.exit(1);
		}

		try (FileReader reader = new FileReader(CONF_FILE)) {
			PROPERTIES.load(reader);
		} catch (IOException err) {
			Config.LOGGER.error("An error occurred while getting API Keys. Exiting.", err);
			System.exit(1);
		}
	}

	public enum APIKey {
		CONSUMER_KEY,
		CONSUMER_SECRET,
		ACCESS_TOKEN,
		ACCESS_TOKEN_SECRET
	}

	public static void addToHistory(long wallpaperID) {
		JSONArray arrayObj = Storage.getHistory();
		arrayObj.put(wallpaperID);

		try (FileWriter writer = new FileWriter(HISTORY_FILE)) {
			writer.write(arrayObj.toString(2));
		} catch (JSONException | IOException err) {
			Config.LOGGER.error("Error while saving history.", err);
		}
	}

	public static JSONArray getHistory() {
		JSONArray arrayObj = new JSONArray();
		try (FileReader reader = new FileReader(HISTORY_FILE)) {
			arrayObj = new JSONArray(new JSONTokener(reader));
		} catch (JSONException | IOException err) {
			Config.LOGGER.error("An error occurred while getting history.", err);
		}
		return arrayObj;
	}

	public static String get(APIKey key) {
		return PROPERTIES.getProperty(key.toString());
	}
}