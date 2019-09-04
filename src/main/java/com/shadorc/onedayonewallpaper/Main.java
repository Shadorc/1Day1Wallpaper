package com.shadorc.onedayonewallpaper;

import com.shadorc.onedayonewallpaper.utils.Utils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.time.Duration;

public class Main {

    private static final Logger LOGGER = Loggers.getLogger("1day1wallpaper");
    private static final WallpaperManager WALLPAPER_MANAGER = new WallpaperManager();

    public static void main(String[] args) {
        TwitterAPI.connect();

        final Duration delay = Utils.getNextPost();

        LOGGER.info("Delay before next tweet: {}", delay);
        Flux.interval(delay, Duration.ofDays(1))
                .flatMap(ignored -> WALLPAPER_MANAGER.post())
                .retryBackoff(3, Duration.ofMinutes(1))
                .onErrorContinue((err, obj) -> Mono.fromRunnable(() -> LOGGER.error("An unknown error occurred.", err)))
                .blockLast();
    }

}
