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

public final class TwitterAPI {

    private static final Logger LOGGER = Loggers.getLogger(TwitterAPI.class);

    private static TwitterAPI instance;

    static {
        TwitterAPI.instance = new TwitterAPI();
    }

    private Twitter twitter;

    public void connect() {
        if (this.twitter == null) {
            LOGGER.info("Connection to Twitter...");
            this.twitter = TwitterFactory.getSingleton();
            this.twitter.setOAuthConsumer(CredentialManager.getInstance().get(Credential.CONSUMER_KEY),
                    CredentialManager.getInstance().get(Credential.CONSUMER_SECRET));
            this.twitter.setOAuthAccessToken(new AccessToken(CredentialManager.getInstance().get(Credential.ACCESS_TOKEN),
                    CredentialManager.getInstance().get(Credential.ACCESS_TOKEN_SECRET)));
            LOGGER.info("Connected to Twitter.");
        }
    }

    public boolean hasPostedToday() throws TwitterException {
        final ResponseList<Status> timeline = this.twitter.getUserTimeline();
        if (timeline.isEmpty()) {
            return false;
        }
        final Instant lastTweetInstant = timeline.get(0).getCreatedAt().toInstant();
        final ZonedDateTime lastTweetDate = ZonedDateTime.ofInstant(lastTweetInstant, ZoneId.systemDefault());
        return lastTweetDate.getDayOfYear() == ZonedDateTime.now().getDayOfYear();
    }

    public Status tweet(final StatusUpdate statusUpdate) throws TwitterException {
        LOGGER.info("Posting tweet...");
        final Status status = this.twitter.updateStatus(statusUpdate);
        LOGGER.info("Tweet posted.");
        return status;
    }

    public static TwitterAPI getInstance() {
        return TwitterAPI.instance;
    }
}