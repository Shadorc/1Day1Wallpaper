package me.shadorc.onedayonewallpaper.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.shadorc.onedayonewallpaper.Config;
import twitter4j.JSONArray;

public class Utils {

	public static long getDelayBeforeNextPost() {
		// The default posting time has passed and no tweet has been sent
		if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= Config.POST_HOUR
				&& !TwitterUtils.hasPostedToday()) {
			return 0;
		}

		final ZonedDateTime zonedNow = ZonedDateTime.now();
		ZonedDateTime zonedNext = zonedNow.withHour(Config.POST_HOUR).withMinute(0).withSecond(0);
		if(zonedNow.isAfter(zonedNext)) {
			zonedNext = zonedNext.plusDays(1);
		}

		return Duration.between(ZonedDateTime.now(), zonedNext).toMillis();
	}

	public static List<Long> toList(JSONArray array) {
		final List<Long> list = new ArrayList<>();
		for(int i = 0; i < array.length(); i++) {
			list.add(array.getLong(i));
		}
		return list;
	}

	public static void saveImage(String url, File file) throws IOException {
		URLConnection connection = new URL(url).openConnection();
		connection.setRequestProperty("User-Agent", Config.USER_AGENT);
		try (InputStream in = connection.getInputStream()) {
			Files.copy(in, file.toPath());
		}
	}
}