package com.shadorc.onedayonewallpaper;

import com.shadorc.onedayonewallpaper.api.TwitterAPI;
import com.shadorc.onedayonewallpaper.api.wallhaven.WallhavenResponse;
import com.shadorc.onedayonewallpaper.api.wallhaven.Wallpaper;
import com.shadorc.onedayonewallpaper.utils.NetUtils;
import com.shadorc.onedayonewallpaper.utils.Utils;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;
import twitter4j.Status;
import twitter4j.StatusUpdate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class WallpaperManager {

    private static final Logger LOGGER = Loggers.getLogger(WallpaperManager.class);
    private static final String URL = "https://wallhaven.cc/api/v1/search?" +
            "sorting=toplist" +
            "&purity=100" +
            "&atleast=1920x1080";
    private static final List<String> BLACKLISTED_TAGS = List.of("car", "woman", "women", "pornstar",
            "brunette", "blonde");

    // Twitter image size restriction: https://developer.twitter.com/en/docs/media/upload-media/overview
    private static final float FILE_SIZE_LIMIT = 4.5f * 1000 * 1000;

    private static WallpaperManager instance;

    static {
        WallpaperManager.instance = new WallpaperManager();
    }

    public Mono<Status> post() {
        LOGGER.info("Getting random wallpaper... ");

        return NetUtils.get(URL, WallhavenResponse.class)
                .flatMapIterable(WallhavenResponse::getWallpapers)
                .filter(WallpaperManager::isWallpaperValid)
                .collectList()
                .filter(Predicate.not(List::isEmpty))
                .map(list -> list.get(ThreadLocalRandom.current().nextInt(list.size())))
                .flatMap(wallpaper -> Mono.fromCallable(() -> {
                    Utils.saveImage(wallpaper.getPath(), Storage.getInstance().getImageFile());
                    Storage.getInstance().addToHistory(wallpaper.getId());

                    final StatusUpdate statusUpdate =
                            new StatusUpdate(wallpaper.getShortUrl() + "\nResolution : " + wallpaper.getResolution());
                    statusUpdate.setMedia(Storage.getInstance().getImageFile());
                    return statusUpdate;
                }))
                .flatMap(status -> Mono.fromCallable(() -> TwitterAPI.getInstance().tweet(status)))
                .switchIfEmpty(Mono.error(new RuntimeException("No wallpaper found.")));
    }

    private static boolean isWallpaperValid(final Wallpaper wallpaper) {
        return wallpaper.getFileSize() < FILE_SIZE_LIMIT
                && !Utils.toList(Storage.getInstance().getHistory(), String.class).contains(wallpaper.getId())
                && wallpaper.getRatio() >= 1.6
                && wallpaper.getRatio() <= 1.8
                && WallpaperManager.areTagsValid(wallpaper);
    }

    private static boolean areTagsValid(final Wallpaper wallpaper) {
        return wallpaper.getTags()
                .stream()
                .allMatch(tag -> tag.getPurity().equals("sfw") && !BLACKLISTED_TAGS.contains(tag.getName()));
    }

    public static WallpaperManager getInstance() {
        return WallpaperManager.instance;
    }
}