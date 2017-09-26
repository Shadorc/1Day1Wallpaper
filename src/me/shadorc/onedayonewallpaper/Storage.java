package me.shadorc.onedayonewallpaper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import me.shadorc.onedayonewallpaper.utils.Utils;

public class Storage {

	private static final Properties PROPERTIES = new Properties();

	private static final File CONF_FILE = new File("config.properties");
	private static final File HISTORY_FILE = new File("history.json");

	public static final File IMAGE_FILE = new File("image.jpg");

	static {
		if(!HISTORY_FILE.exists()) {
			FileWriter writer = null;
			try {
				HISTORY_FILE.createNewFile();
				writer = new FileWriter(HISTORY_FILE);
				writer.write(new JSONArray().toString());
				writer.flush();

			} catch (IOException e) {
				Config.LOGGER.error("An error occured during the initialization of the data file. Exiting.", e);
				System.exit(1);

			} finally {
				Utils.closeQuietly(writer);
			}
		}

		if(!CONF_FILE.exists()) {
			Config.LOGGER.error("Properties file is missing. Exiting.");
			System.exit(1);
		}

		InputStream inStream = null;
		try {
			inStream = new FileInputStream(CONF_FILE);
			PROPERTIES.load(inStream);

		} catch (IOException err) {
			Config.LOGGER.error("An error occured while getting API Keys. Exiting.", err);
			System.exit(1);

		} finally {
			Utils.closeQuietly(inStream);
		}
	}

	public enum APIKey {
		CONSUMER_KEY,
		CONSUMER_SECRET,
		ACCESS_TOKEN,
		ACCESS_TOKEN_SECRET
	}

	public static void addToHistory(long wallpaperID) {
		FileWriter writer = null;
		try {
			JSONArray arrayObj = new JSONArray(new JSONTokener(HISTORY_FILE.toURI().toURL().openStream()));
			arrayObj.put(wallpaperID);

			writer = new FileWriter(HISTORY_FILE);
			writer.write(arrayObj.toString(2));
			writer.flush();

		} catch (JSONException | IOException err) {
			Config.LOGGER.error("Error while saving history.", err);

		} finally {
			Utils.closeQuietly(writer);
		}
	}

	public static JSONArray getHistory() {
		JSONArray arrayObj = new JSONArray();
		try {
			arrayObj = new JSONArray(new JSONTokener(HISTORY_FILE.toURI().toURL().openStream()));
		} catch (JSONException | IOException err) {
			Config.LOGGER.error("An error occured while getting history.", err);
		}
		return arrayObj;
	}

	public static String get(APIKey key) {
		return PROPERTIES.getProperty(key.toString());
	}
}