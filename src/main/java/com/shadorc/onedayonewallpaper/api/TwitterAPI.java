package com.shadorc.onedayonewallpaper.api;

import com.shadorc.onedayonewallpaper.data.credential.Credential;
import com.shadorc.onedayonewallpaper.data.credential.CredentialManager;
import reactor.util.Logger;
import reactor.util.Loggers;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TwitterAPI {

    private static final Logger LOGGER = Loggers.getLogger(TwitterAPI.class);

    private static Twitter twitter;

    public static synchronized void connect() {
        if (TwitterAPI.twitter == null) {
            LOGGER.info("Connecting to Twitter...");
            TwitterAPI.twitter = TwitterFactory.getSingleton();
            TwitterAPI.twitter.setOAuthConsumer(
                    CredentialManager.get(Credential.CONSUMER_KEY),
                    CredentialManager.get(Credential.CONSUMER_SECRET));
            TwitterAPI.twitter.setOAuthAccessToken(new AccessToken(
                    CredentialManager.get(Credential.ACCESS_TOKEN),
                    CredentialManager.get(Credential.ACCESS_TOKEN_SECRET)));
            LOGGER.info("Connected to Twitter");
        }
    }

    public static boolean hasPostedToday() throws TwitterException {
        final ResponseList<Status> timeline = TwitterAPI.twitter.getUserTimeline();
        if (timeline.isEmpty()) {
            return false;
        }
        final Instant lastTweetInstant = timeline.get(0).getCreatedAt().toInstant();
        final ZonedDateTime lastTweetDate = ZonedDateTime.ofInstant(lastTweetInstant, ZoneId.systemDefault());
        return lastTweetDate.getDayOfYear() == ZonedDateTime.now().getDayOfYear();
    }

    public static Status tweet(StatusUpdate statusUpdate) throws TwitterException {
        LOGGER.info("Posting tweet...");
        final Status status = TwitterAPI.twitter.updateStatus(statusUpdate);
        LOGGER.info("Tweet posted");
        return status;
    }
}