package me.shadorc.onedayonewallpaper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

import me.shadorc.onedayonewallpaper.utils.LogUtils;

public class Config {

	private static final Properties PROPERTIES = Config.getProperties();

	public static final String CONSUMER_KEY = PROPERTIES.getProperty("consumer.key");
	public static final String CONSUMER_SECRET = PROPERTIES.getProperty("consumer.secret");
	public static final String ACCESS_TOKEN = PROPERTIES.getProperty("access.token");
	public static final String ACCESS_TOKEN_SECRET = PROPERTIES.getProperty("access.token.secret");

	public static final int POST_HOUR = Integer.parseInt(PROPERTIES.getProperty("post.hour"));
	public static final int MIN_VIEWS = Integer.parseInt(PROPERTIES.getProperty("min.views"));

	public static final String USER_AGENT = PROPERTIES.getProperty("user.agent");

	private static Properties getProperties() {
		final Properties properties = new Properties();
		try (InputStream inputStream = Files.newInputStream(new File("project.properties").toPath())) {
			if(inputStream != null) {
				properties.load(inputStream);
			}
		} catch (final IOException err) {
			LogUtils.error("An error occurred while loading configuration file. Exiting.", err);
			System.exit(1);
		}
		return properties;
	}

}
