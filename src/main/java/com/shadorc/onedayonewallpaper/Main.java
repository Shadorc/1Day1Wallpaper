package com.shadorc.onedayonewallpaper;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.shadorc.onedayonewallpaper.utils.LogUtils;
import com.shadorc.onedayonewallpaper.utils.TwitterUtils;
import com.shadorc.onedayonewallpaper.utils.Utils;

public class Main {

	private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

	public static void main(String[] args) {
		LogUtils.info("Connection to Twitter...");
		TwitterUtils.connection();
		LogUtils.info("Connected to Twitter.");

		final Runnable postingThread = new Runnable() {
			@Override
			public void run() {
				try {
					WallpaperManager.post();
				} catch (IOException err) {
					LogUtils.error("Something went wrong while posting image, retrying in 1 minute.", err);
					EXECUTOR.schedule(this, Duration.ofMinutes(1).toMinutes(), TimeUnit.MINUTES);
				}
			}
		};

		LogUtils.info("Next post: %s", Duration.ofMillis(Utils.getDelayBeforeNextPost()));
		EXECUTOR.scheduleAtFixedRate(postingThread, Utils.getDelayBeforeNextPost(),
				Duration.ofDays(1).toMillis(), TimeUnit.MILLISECONDS);
	}

}
