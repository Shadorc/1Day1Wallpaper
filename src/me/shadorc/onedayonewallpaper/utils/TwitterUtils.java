package me.shadorc.onedayonewallpaper.utils;

import java.util.Calendar;

import me.shadorc.onedayonewallpaper.Config;
import me.shadorc.onedayonewallpaper.Storage;
import me.shadorc.onedayonewallpaper.Storage.APIKey;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterUtils {

	private static Twitter twitter;

	public static void connection() {
		twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer(Storage.get(APIKey.CONSUMER_KEY), Storage.get(APIKey.CONSUMER_SECRET));
		twitter.setOAuthAccessToken(new AccessToken(Storage.get(APIKey.ACCESS_TOKEN), Storage.get(APIKey.ACCESS_TOKEN_SECRET)));
	}

	public static boolean hasPostedToday() {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(twitter.getUserTimeline().get(0).getCreatedAt());
			return calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		} catch (TwitterException e) {
			Config.LOGGER.error("An error occurred while getting last tweet.", e);
			return true;
		}
	}

	public static void tweet(StatusUpdate status) {
		try {
			twitter.updateStatus(status);
		} catch (TwitterException e) {
			Config.LOGGER.error("An error occurred while updating status.", e);
		}
	}
}