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

import java.util.concurrent.ThreadLocalRandom;

public class WallpaperManager {

    private static final Logger LOGGER = Loggers.getLogger(WallpaperManager.class);
    private static final String URL = "https://wallhaven.cc/api/v1/search?" +
            "sorting=toplist" +
            "&purity=100" +
            "&atleast=1920x1080" +
            "&q=-car-woman-women-pornstar-brunette-blonde";

    // Twitter image size restriction: https://developer.twitter.com/en/docs/media/upload-media/overview
    private static final int FILE_SIZE_LIMIT = 5 * 1024 * 1024;

    public Mono<Status> post() {
        LOGGER.info("Getting random wallpaper... ");

        return NetUtils.get(URL, WallhavenResponse.class)
                .flatMapIterable(WallhavenResponse::getWallpapers)
                .filter(this::isWallpaperValid)
                .collectList()
                .map(list -> list.get(ThreadLocalRandom.current().nextInt(list.size())))
                .flatMap(wallpaper -> Mono.fromCallable(() -> {
                    Utils.saveImage(wallpaper.getPath(), Storage.getInstance().getImageFile());
                    Storage.getInstance().addToHistory(wallpaper.getId());

                    final StatusUpdate statusUpdate =
                            new StatusUpdate(wallpaper.getShortUrl() + "\nResolution : " + wallpaper.getResolution());
                    statusUpdate.setMedia(Storage.getInstance().getImageFile());
                    return statusUpdate;
                }))
                .flatMap(status -> Mono.fromCallable(() -> TwitterAPI.getInstance().tweet(status)));
    }

    private boolean isWallpaperValid(final Wallpaper wallpaper) {
        return wallpaper.getFileSize() < FILE_SIZE_LIMIT
                && !Utils.toList(Storage.getInstance().getHistory(), String.class).contains(wallpaper.getId())
                && wallpaper.getRatio() >= 1.6
                && wallpaper.getRatio() <= 1.8;
    }
}