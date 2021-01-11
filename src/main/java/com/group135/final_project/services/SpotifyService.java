package com.group135.final_project.services;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This service wraps the spotify API library.
 */
@Service
public class SpotifyService {
    private static final String clientId = "384237b4e98248f0b1d5ee4f901f6f7c";
    private static final String clientSecret = "9e96c4c77ff64ba881aca2596c02b6ad";

    public static void main(String[] args) throws ParseException, SpotifyWebApiException, IOException {
        var artistNames = new SpotifyService().fetchArtistsFromPlaylist(
                "37i9dQZEVXbMDoHDwVN2tF"
        ).stream().map(ArtistSimplified::getName).collect(Collectors.toList());

        for (var name : artistNames) {
            System.out.println(name);
        }
    }

    /**
     * Fetches all {@link ArtistSimplified} from every track in the playlist.
     */
    public List<ArtistSimplified> fetchArtistsFromPlaylist(String id) throws ParseException, SpotifyWebApiException, IOException {
        var items = buildClient()
            .getPlaylist(id)
            .build()
            .execute()
            .getTracks()
            .getItems();

        // TODO: As we convert this to a list and not a set, an artist might
        //       appear twice. We need to figure out how to filter out duplicates
        return Arrays.stream(items)
                .map(PlaylistTrack::getTrack)
                .filter(track -> track instanceof Track)
                .map(item -> (Track) item)
                .flatMap(track -> Arrays.stream(track.getArtists()))
                .collect(Collectors.toList());
    }

    private SpotifyApi buildClient() throws ParseException, SpotifyWebApiException, IOException {
        var api = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();

        // TODO: We do not need this every time, but for now it is easier
        ClientCredentialsRequest clientCredentialsRequest = api.clientCredentials().build();
        ClientCredentials clientCred = clientCredentialsRequest.execute();
        api.setAccessToken(clientCred.getAccessToken());

        return api;
    }
}
