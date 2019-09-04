package com.shadorc.onedayonewallpaper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.shadorc.onedayonewallpaper.utils.LogUtils;
import twitter4j.JSONArray;
import twitter4j.JSONTokener;

public class Storage {

	private static final File HISTORY_FILE = new File("history.json");
	public static final File IMAGE_FILE = new File("image.jpg");

	static {
		if(!HISTORY_FILE.exists()) {
			try (FileWriter writer = new FileWriter(HISTORY_FILE)) {
				HISTORY_FILE.createNewFile();
				writer.write(new JSONArray().toString());
			} catch (IOException err) {
				LogUtils.error("An error occurred during the initialization of the data file. Exiting.", err);
				System.exit(1);
			}
		}
	}

	public static void addToHistory(long wallpaperId) {
		JSONArray arrayObj = Storage.getHistory();
		arrayObj.put(wallpaperId);

		try (FileWriter writer = new FileWriter(HISTORY_FILE)) {
			writer.write(arrayObj.toString(2));
		} catch (IOException err) {
			LogUtils.error("An error occurred while saving history.", err);
		}
	}

	public static JSONArray getHistory() {
		JSONArray arrayObj = new JSONArray();
		try (FileReader reader = new FileReader(HISTORY_FILE)) {
			arrayObj = new JSONArray(new JSONTokener(reader));
		} catch (IOException err) {
			LogUtils.error("An error occurred while getting history.", err);
		}
		return arrayObj;
	}

}