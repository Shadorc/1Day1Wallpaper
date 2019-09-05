package com.shadorc.onedayonewallpaper;

import com.shadorc.onedayonewallpaper.data.Credential;
import com.shadorc.onedayonewallpaper.data.Credentials;
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

    public static void connect() {
        if (twitter == null) {
            LOGGER.info("Connection to Twitter...");
            twitter = TwitterFactory.getSingleton();
            twitter.setOAuthConsumer(Credentials.get(Credential.CONSUMER_KEY), Credentials.get(Credential.CONSUMER_SECRET));
            twitter.setOAuthAccessToken(new AccessToken(Credentials.get(Credential.ACCESS_TOKEN), Credentials.get(Credential.ACCESS_TOKEN_SECRET)));
            LOGGER.info("Connected to Twitter.");
        }
    }

    public static boolean hasPostedToday() throws TwitterException {
        final ResponseList<Status> timeline = twitter.getUserTimeline();
        if (timeline.isEmpty()) {
            return false;
        }
        final Instant lastTweetInstant = timeline.get(0).getCreatedAt().toInstant();
        final ZonedDateTime lastTweetDate = ZonedDateTime.ofInstant(lastTweetInstant, ZoneId.systemDefault());
        return lastTweetDate.getDayOfYear() == ZonedDateTime.now().getDayOfYear();
    }

    public static Status tweet(final StatusUpdate statusUpdate) throws TwitterException {
        LOGGER.info("Posting tweet...");
        final Status status = twitter.updateStatus(statusUpdate);
        LOGGER.info("Tweet posted.");
        return status;
    }
}