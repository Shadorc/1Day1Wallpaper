package com.shadorc.onedayonewallpaper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.ivkos.wallhaven4j.Wallhaven;
import com.ivkos.wallhaven4j.models.misc.enums.Category;
import com.ivkos.wallhaven4j.models.misc.enums.Purity;
import com.ivkos.wallhaven4j.models.misc.enums.Sorting;
import com.ivkos.wallhaven4j.models.tag.Tag;
import com.ivkos.wallhaven4j.models.wallpaper.Wallpaper;
import com.ivkos.wallhaven4j.util.searchquery.SearchQuery;
import com.ivkos.wallhaven4j.util.searchquery.SearchQueryBuilder;

import com.shadorc.onedayonewallpaper.utils.LogUtils;
import com.shadorc.onedayonewallpaper.utils.TwitterUtils;
import com.shadorc.onedayonewallpaper.utils.Utils;
import twitter4j.StatusUpdate;

public class WallpaperManager {

	private final static Wallhaven WALLHAVEN = new Wallhaven();

	public static void post() throws IOException {
		LogUtils.info("Connection to WallHaven, getting random wallpaper... ");

		Wallpaper wallpaper;
		do {
			final SearchQuery query = new SearchQueryBuilder()
					.sorting(Sorting.RANDOM)
					.purity(Purity.SFW)
					.pages(1)
					.build();

			final List<Wallpaper> wallpapers = WALLHAVEN.search(query);
			wallpaper = wallpapers.get(ThreadLocalRandom.current().nextInt(wallpapers.size()));
		} while(!WallpaperManager.isValid(wallpaper));

		Utils.saveImage(wallpaper.getImageUrl(), Storage.IMAGE_FILE);
		Storage.addToHistory(wallpaper.getId());

		StatusUpdate status = new StatusUpdate(wallpaper.getShortLink() + "\nResolution : " + wallpaper.getResolution().toString());
		status.setMedia(Storage.IMAGE_FILE);

		LogUtils.info("Posting tweet...");
		TwitterUtils.tweet(status);
		LogUtils.info("Tweet posted.");
	}

	private static boolean isValid(Wallpaper wallpaper) {
		if(Utils.toList(Storage.getHistory()).contains(wallpaper.getId())) {
			LogUtils.info("Retrying... [Wallpaper already posted]");
			return false;
		}

		if(!WallpaperManager.isAppropriateSize(wallpaper)) {
			LogUtils.info("Retrying... [Resolution : " + wallpaper.getResolution() + "]");
			return false;
		}

		if(wallpaper.getViewsCount() < Config.MIN_VIEWS) {
			LogUtils.info("Retrying... [Views : " + wallpaper.getViewsCount() + "/" + Config.MIN_VIEWS + "]");
			return false;
		}

		if(WallpaperManager.containsTag(wallpaper, "women") && wallpaper.getCategory().equals(Category.PEOPLE)) {
			LogUtils.info("Retrying... [Category : 'People', containing #women]");
			return false;
		}

		if(WallpaperManager.containsTag(wallpaper, "car") && wallpaper.getCategory().equals(Category.GENERAL)) {
			LogUtils.info("Retrying... [Category : 'General', containing #car]");
			return false;
		}

		return true;
	}

	private static boolean containsTag(Wallpaper wallpaper, String searchTag) {
		return wallpaper.getTags()
				.stream()
				.map(Tag::getName)
				.anyMatch(searchTag::equals);
	}

	private static boolean isAppropriateSize(Wallpaper wallpaper) {
		float width = wallpaper.getResolution().getWidth();
		float height = wallpaper.getResolution().getHeight();
		float ratio = width / height;

		return width >= 1920 && height >= 1080 && ratio >= 1.6 && ratio <= 1.8;
	}
}