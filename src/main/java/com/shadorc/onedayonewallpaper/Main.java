package com.shadorc.onedayonewallpaper;

import com.shadorc.onedayonewallpaper.api.TwitterAPI;
import com.shadorc.onedayonewallpaper.data.Config;
import com.shadorc.onedayonewallpaper.utils.Utils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;
import reactor.util.retry.Retry;
import twitter4j.StatusUpdate;

import java.time.Duration;

public final class Main {

    private static final Logger LOGGER = Loggers.getLogger("1day1wallpaper");

    public static void main(final String[] args) {
        TwitterAPI.getInstance().connect();

        Mono.fromCallable(Utils::getNextPost)
                .doOnNext(delay -> LOGGER.info("Delay before next tweet: {}",
                        String.format("%dh %02dm %02ds", delay.toHoursPart(), delay.toMinutesPart(), delay.toSecondsPart())))
                .flatMapMany(delay -> Flux.interval(delay, Duration.ofDays(1)))
                .flatMap(ignored -> WallpaperManager.getInstance().requestWallpaper())
                .flatMap(wallpaper -> Mono.fromCallable(() -> {
                    Utils.saveImage(wallpaper.getPath(), Storage.getInstance().getImageFile());
                    Storage.getInstance().addToHistory(wallpaper.getId());

                    final StatusUpdate statusUpdate =
                            new StatusUpdate(wallpaper.getShortUrl() + "\nResolution : " + wallpaper.getResolution());
                    statusUpdate.setMedia(Storage.getInstance().getImageFile());
                    return statusUpdate;
                }))
                .flatMap(status -> Mono.fromCallable(() -> TwitterAPI.getInstance().tweet(status)))
                .doOnError(err -> LOGGER.error("An unknown error occurred. Retrying...", err))
                .retryWhen(Retry.backoff(Config.RETRY_MAX, Duration.ofSeconds(30)))
                .blockLast();
    }

}
