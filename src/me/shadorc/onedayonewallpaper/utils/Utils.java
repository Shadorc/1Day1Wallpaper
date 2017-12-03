package me.shadorc.onedayonewallpaper.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.JSONArray;

import me.shadorc.onedayonewallpaper.Config;

public class Utils {

	public static long getDelayBeforeNextPost() {
		ZonedDateTime zonedNow = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
		ZonedDateTime zonedNext = zonedNow.withHour(Config.POST_HOUR).withMinute(0).withSecond(0);
		if(zonedNow.compareTo(zonedNext) > 0) {
			zonedNext = zonedNext.plusDays(1);
		}

		return zonedNext.toInstant().toEpochMilli() - zonedNow.toInstant().toEpochMilli();
	}

	public static List<Long> toList(JSONArray array) {
		List<Long> list = new ArrayList<>();
		for(int i = 0; i < array.length(); i++) {
			list.add(array.getLong(i));
		}
		return list;
	}

	public static void downloadAndSaveImage(String url, File file) throws IOException {
		URLConnection connection = new URL(url).openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
		connection.connect();
		ImageIO.write(ImageIO.read(connection.getInputStream()), "jpg", file);
	}
}