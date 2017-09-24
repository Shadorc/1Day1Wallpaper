package me.shadorc.onedayonewallpaper;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.ivkos.wallhaven4j.Wallhaven;
import com.ivkos.wallhaven4j.models.misc.enums.Category;
import com.ivkos.wallhaven4j.models.misc.enums.Purity;
import com.ivkos.wallhaven4j.models.misc.enums.Sorting;
import com.ivkos.wallhaven4j.models.wallpaper.Wallpaper;
import com.ivkos.wallhaven4j.util.searchquery.SearchQuery;
import com.ivkos.wallhaven4j.util.searchquery.SearchQueryBuilder;

import me.shadorc.onedayonewallpaper.utils.TwitterUtils;
import me.shadorc.onedayonewallpaper.utils.Utils;
import twitter4j.StatusUpdate;

public class WallpaperManager {

	private final static Wallhaven WALLHAVEN = new Wallhaven();
	private final static Random RAND = new Random();

	public static void post() throws IOException {
		Config.LOGGER.info("Connection to WallHaven, getting random wallpaper... ");

		Wallpaper wallpaper;
		do {
			SearchQuery query = new SearchQueryBuilder()
					.sorting(Sorting.RANDOM)
					.purity(Purity.SFW)
					.pages(1)
					.build();

			List<Wallpaper> wallpapers = WALLHAVEN.search(query);
			wallpaper = wallpapers.get(RAND.nextInt(wallpapers.size()));
		} while(!WallpaperManager.isValid(wallpaper));

		Utils.downloadAndSaveImage(wallpaper.getImageUrl(), Storage.IMAGE_FILE);
		Storage.addToHistory(wallpaper.getId());

		StatusUpdate status = new StatusUpdate("https://whvn.cc/" + wallpaper.getId() + "\nResolution : " + wallpaper.getResolution().toString());
		status.setMedia(Storage.IMAGE_FILE);

		Config.LOGGER.info("Posting tweet...");
		TwitterUtils.tweet(status);
		Config.LOGGER.info("Tweet posted.");
	}

	public static boolean isValid(Wallpaper wallpaper) {
		if(Utils.toList(Storage.getHistory()).contains(wallpaper.getId())) {
			Config.LOGGER.info("Retrying... [Wallpaper already posted]");
			return false;
		}

		if(!WallpaperManager.isAppropriateSize(wallpaper)) {
			Config.LOGGER.info("Retrying... [Resolution : " + wallpaper.getResolution() + "]");
			return false;
		}

		if(wallpaper.getViewsCount() < Config.MIN_VIEWS) {
			Config.LOGGER.info("Retrying... [Views : " + wallpaper.getViewsCount() + "/" + Config.MIN_VIEWS + "]");
			return false;
		}

		if(WallpaperManager.containsTag(wallpaper, "women") && wallpaper.getCategory().equals(Category.PEOPLE)) {
			Config.LOGGER.info("Retrying... [Category : 'People', containing #women]");
			return false;
		}

		if(WallpaperManager.containsTag(wallpaper, "car") && wallpaper.getCategory().equals(Category.GENERAL)) {
			Config.LOGGER.info("Retrying... [Category : 'General', containing #car]");
			return false;
		}

		return true;
	}

	public static boolean containsTag(Wallpaper wallpaper, String searchTag) {
		return wallpaper.getTags().stream().filter(tag -> tag.getName().equals(searchTag)).count() != 0;
	}

	public static boolean isAppropriateSize(Wallpaper wallpaper) {
		float width = wallpaper.getResolution().getWidth();
		float height = wallpaper.getResolution().getHeight();
		float ratio = width / height;

		return width >= 1920 && height >= 1080 && ratio >= 1.6 && ratio <= 1.8;
	}
}