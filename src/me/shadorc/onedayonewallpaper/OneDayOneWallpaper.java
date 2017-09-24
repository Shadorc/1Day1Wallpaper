package me.shadorc.onedayonewallpaper;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import me.shadorc.onedayonewallpaper.utils.TwitterUtils;
import me.shadorc.onedayonewallpaper.utils.Utils;

public class OneDayOneWallpaper {

	public static void main(String[] args) {
		Config.LOGGER.info("Connection to Twitter...");
		TwitterUtils.connection();
		Config.LOGGER.info("Connected to Twitter.");

		Runnable postingThread = new Runnable() {
			@Override
			public void run() {
				try {
					WallpaperManager.post();
				} catch (IOException err) {
					Config.LOGGER.error("Something went wrong while posting image, retrying in 1 minute.", err);
					Executors.newSingleThreadScheduledExecutor().schedule(this, TimeUnit.MINUTES.toMillis(1), TimeUnit.MILLISECONDS);
				}
			}
		};

		// The default posting time has passed and no tweet has been sent
		if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= Config.POST_HOUR && !TwitterUtils.hasPostedToday()) {
			postingThread.run();
		}

		Executors.newSingleThreadScheduledExecutor()
				.scheduleAtFixedRate(postingThread, Utils.getDelayBeforeNextPost(), TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
	}
}
