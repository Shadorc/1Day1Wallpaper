package com.shadorc.onedayonewallpaper;

import com.shadorc.onedayonewallpaper.utils.NetUtils;
import com.shadorc.onedayonewallpaper.utils.Utils;
import com.shadorc.onedayonewallpaper.wallhaven.WallhavenResponse;
import com.shadorc.onedayonewallpaper.wallhaven.Wallpaper;
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

    public Mono<Status> post() {
        LOGGER.info("Getting random wallpaper... ");

        return NetUtils.get(URL, WallhavenResponse.class)
                .flatMapIterable(WallhavenResponse::getWallpapers)
                .filter(this::isWallpaperValid)
                .collectList()
                .map(list -> list.get(ThreadLocalRandom.current().nextInt(list.size())))
                .flatMap(wallpaper -> Mono.fromCallable(() -> {
                    Utils.saveImage(wallpaper.getPath(), Storage.IMAGE_FILE);
                    Storage.addToHistory(wallpaper.getId());

                    final StatusUpdate statusUpdate = new StatusUpdate(wallpaper.getShortUrl() + "\nResolution : " + wallpaper.getResolution());
                    statusUpdate.setMedia(Storage.IMAGE_FILE);
                    return statusUpdate;
                }))
                .flatMap(status -> Mono.fromCallable(() -> TwitterAPI.tweet(status)));
    }

    private boolean isWallpaperValid(final Wallpaper wallpaper) {
        // TODO: Cache history
        return wallpaper.getFileSize() < 5_000_000 // 5Mb is the Twitter API limit for images
                && !Utils.toList(Storage.getHistory(), String.class).contains(wallpaper.getId())
                && wallpaper.getRatio() >= 1.6
                && wallpaper.getRatio() <= 1.8;
    }
}