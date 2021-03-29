package com.shadorc.onedayonewallpaper;

import com.shadorc.onedayonewallpaper.api.wallhaven.Tag;
import com.shadorc.onedayonewallpaper.api.wallhaven.WallhavenResponse;
import com.shadorc.onedayonewallpaper.api.wallhaven.Wallpaper;
import com.shadorc.onedayonewallpaper.data.Storage;
import com.shadorc.onedayonewallpaper.utils.NetUtils;
import com.shadorc.onedayonewallpaper.utils.Utils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class WallpaperManager {

    private static final Logger LOGGER = Loggers.getLogger(WallpaperManager.class);

    private static final String API_URL = "https://wallhaven.cc/api/v1";
    private static final String SEARCH_URL = String.format("%s/search?" +
            "sorting=toplist" +
            "&purity=100" +
            "&atleast=1920x1080" +
            "&ratios=16x9", API_URL);
    private static final String WALLPAPER_URL = String.format("%s/w", API_URL);
    private static final List<String> BLACKLISTED_TAGS =
            List.of("car", "woman", "women", "pornstar", "brunette", "blonde");

    // Twitter image size restriction: https://developer.twitter.com/en/docs/media/upload-media/overview
    private static final long FILE_SIZE_LIMIT = (long) (4.5f * 1000 * 1000);

    public static Mono<Wallpaper> requestWallpaper() {
        LOGGER.info("Getting random wallpaper...");

        return NetUtils.get(SEARCH_URL, WallhavenResponse.class)
                .flatMapIterable(WallhavenResponse::getWallpapers)
                .filter(WallpaperManager::isWallpaperValid)
                .collectList()
                .filter(Predicate.not(List::isEmpty))
                .map(list -> {
                    final List<Wallpaper> shuffledList = new ArrayList<>(list);
                    Collections.shuffle(shuffledList);
                    return shuffledList;
                })
                .flatMapMany(Flux::fromIterable)
                .flatMap(wallpaper -> NetUtils.get(String.format("%s/%s", WALLPAPER_URL, wallpaper.getId()), WallhavenResponse.class))
                .flatMapIterable(WallhavenResponse::getWallpapers)
                .filter(WallpaperManager::areTagsValid)
                .filterWhen(wallpaper -> Mono.fromCallable(() -> Utils.saveImage(wallpaper.getPath(), Storage.getImageFile()))
                        .map(writtenBytes -> {
                            if (writtenBytes > FILE_SIZE_LIMIT) {
                                LOGGER.debug("Wallpaper {} is not valid (File size: {}MB / {}MB)",
                                        wallpaper.getShortUrl(), writtenBytes / Utils.MB, FILE_SIZE_LIMIT / Utils.MB);
                                return false;
                            }
                            return true;
                        }))
                .next()
                .switchIfEmpty(Mono.error(new RuntimeException("No wallpaper found")));
    }

    private static boolean isWallpaperValid(Wallpaper wallpaper) {
        if (Utils.toList(Storage.getHistory(), String.class).contains(wallpaper.getId())) {
            LOGGER.debug("Wallpaper {} is not valid (Already posted)", wallpaper.getShortUrl());
            return false;
        }
        if (wallpaper.getRatio() < 1.6 || wallpaper.getRatio() > 1.8) {
            LOGGER.debug("Wallpaper {} is not valid (Ratio: {})", wallpaper.getShortUrl(), wallpaper.getRatio());
            return false;
        }
        return true;
    }

    private static boolean areTagsValid(Wallpaper wallpaper) {
        return wallpaper.getTags()
                .map(tags -> {
                    for (final Tag tag : tags) {
                        if (!"sfw".equals(tag.getPurity())) {
                            LOGGER.debug("Wallpaper tag is not valid ({} is {})", tag.getName(), tag.getPurity());
                            return false;
                        }
                        if (BLACKLISTED_TAGS.contains(tag.getName())) {
                            LOGGER.debug("Wallpaper tag is not valid ({} is blacklisted)", tag.getName());
                            return false;
                        }
                    }
                    return true;
                })
                .orElse(true);
    }
}