package com.shadorc.onedayonewallpaper.utils;

import com.shadorc.onedayonewallpaper.api.TwitterAPI;
import com.shadorc.onedayonewallpaper.data.Config;
import twitter4j.JSONArray;
import twitter4j.TwitterException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public final class Utils {

    public static Duration getNextPost() throws TwitterException {
        final ZonedDateTime nowDate = ZonedDateTime.now(ZoneId.systemDefault());

        // The wallpaper should have been posted already but it is not the case
        if (nowDate.getHour() >= Config.POST_HOUR && !TwitterAPI.getInstance().hasPostedToday()) {
            return Duration.ZERO;
        }

        ZonedDateTime zonedNext = ZonedDateTime.now(ZoneId.systemDefault()).withHour(Config.POST_HOUR)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        if (nowDate.isAfter(zonedNext)) {
            zonedNext = zonedNext.plusDays(1);
        }

        return Duration.between(nowDate, zonedNext);
    }

    public static <T> List<T> toList(final JSONArray array, final Class<? extends T> type) {
        final List<T> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(type.cast(array.get(i)));
        }
        return list;
    }

    public static void saveImage(final String url, final File file) throws IOException {
        final URLConnection connection = new URL(url).openConnection();
        try (final InputStream in = connection.getInputStream()) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}