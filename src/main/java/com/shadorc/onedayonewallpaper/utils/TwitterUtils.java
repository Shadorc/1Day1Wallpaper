package com.shadorc.onedayonewallpaper.utils;

import java.util.Calendar;

import com.shadorc.onedayonewallpaper.Config;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterUtils {

	private static Twitter twitter;

	public static void connection() {
		twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer(Config.CONSUMER_KEY, Config.CONSUMER_SECRET);
		twitter.setOAuthAccessToken(new AccessToken(Config.ACCESS_TOKEN, Config.ACCESS_TOKEN_SECRET));
	}

	public static boolean hasPostedToday() {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(twitter.getUserTimeline().get(0).getCreatedAt());
			return calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		} catch (TwitterException err) {
			LogUtils.error("An error occurred while getting last tweet.", err);
			return true;
		}
	}

	public static void tweet(StatusUpdate status) {
		try {
			twitter.updateStatus(status);
		} catch (TwitterException err) {
			LogUtils.error("An error occurred while updating status.", err);
		}
	}
}