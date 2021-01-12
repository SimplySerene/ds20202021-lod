package com.group135.final_project.services;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.apache.hc.core5.http.ParseException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Handles the access to a {@link SpotifyApi} instance, so we centralize
 * the management of access tokens.
 *
 * Intended for the use in {@link SpotifyService}!
 */
@Service
public class Spotify {
    private static final String clientId = "384237b4e98248f0b1d5ee4f901f6f7c";
    private static final String clientSecret = "9e96c4c77ff64ba881aca2596c02b6ad";

    private final SpotifyApi api = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .build();

    /**
     * This will be used to track if we need a new access token.
     */
    @Nullable
    private LocalDateTime expiresIn;

    /**
     * This should be called *before* a request is made using the returned client instance.
     *
     * !!!PLEASE DO NOT SAVE THE RETURNED CLIENT INSTANCE!!!
     */
    public SpotifyApi apiClient() throws ParseException, SpotifyWebApiException, IOException {
        if (needsFreshAccessToken()) {
            refreshAccessToken();
        }

        return api;
    }

    private void refreshAccessToken() throws ParseException, SpotifyWebApiException, IOException {
        var credentials = api.clientCredentials().build().execute();
        var token = credentials.getAccessToken();

        api.setAccessToken(token);
        expiresIn = LocalDateTime.now().plusSeconds(credentials.getExpiresIn());
    }

    private boolean needsFreshAccessToken() {
        return expiresIn == null
                // We refresh 1 minute too early, just to be sure
                || expiresIn.isAfter(LocalDateTime.now().minusMinutes(1));
    }
}