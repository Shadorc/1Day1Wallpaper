package com.shadorc.onedayonewallpaper;

import com.shadorc.onedayonewallpaper.api.wallhaven.WallhavenResponse;
import com.shadorc.onedayonewallpaper.api.wallhaven.Wallpaper;
import com.shadorc.onedayonewallpaper.utils.NetUtils;
import com.shadorc.onedayonewallpaper.utils.Utils;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class WallpaperManager {

    private static final Logger LOGGER = Loggers.getLogger(WallpaperManager.class);

    private static final String API_URL = "https://wallhaven.cc/api/v1";
    private static final String SEARCH_URL = API_URL + "/search?" +
            "sorting=toplist" +
            "&purity=100" +
            "&atleast=1920x1080";
    private static final String WALLPAPER_URL = API_URL + "/w";
    private static final List<String> BLACKLISTED_TAGS = List.of("car", "woman", "women", "pornstar",
            "brunette", "blonde");

    // Twitter image size restriction: https://developer.twitter.com/en/docs/media/upload-media/overview
    private static final float FILE_SIZE_LIMIT = 4.5f * 1000 * 1000;

    private static WallpaperManager instance;

    static {
        WallpaperManager.instance = new WallpaperManager();
    }

    public Mono<Wallpaper> requestWallpaper() {
        LOGGER.info("Getting random wallpaper... ");

        return NetUtils.get(SEARCH_URL, WallhavenResponse.class)
                .flatMapIterable(WallhavenResponse::getWallpapers)
                .filter(WallpaperManager::isWallpaperValid)
                .collectList()
                .filter(Predicate.not(List::isEmpty))
                .map(list -> list.get(ThreadLocalRandom.current().nextInt(list.size())))
                .flatMap(wallpaper -> NetUtils.get(WALLPAPER_URL + "/" + wallpaper.getId(), WallhavenResponse.class))
                .flatMapIterable(WallhavenResponse::getWallpapers)
                .filter(WallpaperManager::areTagsValid)
                .next()
                .switchIfEmpty(Mono.error(new RuntimeException("No wallpaper found.")));
    }

    private static boolean isWallpaperValid(final Wallpaper wallpaper) {
        return wallpaper.getFileSize() < FILE_SIZE_LIMIT
                && !Utils.toList(Storage.getInstance().getHistory(), String.class).contains(wallpaper.getId())
                && wallpaper.getRatio() >= 1.6
                && wallpaper.getRatio() <= 1.8;
    }

    private static boolean areTagsValid(final Wallpaper wallpaper) {
        return wallpaper.getTags()
                .map(list -> list.stream()
                        .allMatch(tag -> tag.getPurity().equals("sfw") && !BLACKLISTED_TAGS.contains(tag.getName())))
                .orElse(true);
    }

    public static WallpaperManager getInstance() {
        return WallpaperManager.instance;
    }
}