package com.shadorc.onedayonewallpaper;

import com.shadorc.onedayonewallpaper.api.TwitterAPI;
import com.shadorc.onedayonewallpaper.data.Config;
import com.shadorc.onedayonewallpaper.data.Storage;
import com.shadorc.onedayonewallpaper.utils.Utils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;
import reactor.util.retry.Retry;
import twitter4j.StatusUpdate;

import java.time.Duration;

public class Main {

    private static final Logger LOGGER = Loggers.getLogger("1day1wallpaper");

    public static void main(final String[] args) {
        TwitterAPI.connect();

        Mono.fromCallable(Utils::getNextPost)
                .doOnNext(delay -> LOGGER.info("Delay before next tweet: {}",
                        String.format("%dh %02dm %02ds", delay.toHoursPart(), delay.toMinutesPart(), delay.toSecondsPart())))
                .flatMapMany(delay -> Flux.interval(delay, Duration.ofDays(1)))
                .flatMap(__ -> WallpaperManager.requestWallpaper())
                .flatMap(wallpaper -> Mono.fromCallable(() -> {
                    Storage.addToHistory(wallpaper.getId());

                    final StatusUpdate statusUpdate =
                            new StatusUpdate(String.format("%s\nResolution: %s", wallpaper.getResolution(), wallpaper.getShortUrl()));
                    statusUpdate.setMedia(Storage.getImageFile());
                    return statusUpdate;
                }))
                .flatMap(status -> Mono.fromCallable(() -> TwitterAPI.tweet(status)))
                .doOnError(err -> LOGGER.error("An unknown error occurred, retrying...", err))
                .retryWhen(Retry.backoff(Config.RETRY_MAX, Duration.ofSeconds(30)))
                .blockLast();
    }

}
