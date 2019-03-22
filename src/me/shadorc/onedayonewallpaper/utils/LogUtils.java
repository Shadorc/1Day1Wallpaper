package me.shadorc.onedayonewallpaper.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger("1day1wallpaper");

	public static void info(String format, Object... args) {
		LOGGER.info(String.format(format, args));
	}

	public static void error(String msg, Throwable err) {
		LOGGER.error(msg, err);
	}

}
