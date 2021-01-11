package com.group135.final_project.services;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This service wraps the spotify API library.
 */
@Service
public class SpotifyService {
    private final Spotify spotify;

    public SpotifyService(Spotify spotify) {
        this.spotify = spotify;
    }

    /**
     * Fetches all {@link ArtistSimplified} from every track in the playlist.
     */
    public List<ArtistSimplified> fetchArtistsFromPlaylist(String id) throws ParseException, SpotifyWebApiException, IOException {
        var items = spotify.apiClient()
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

    public PlaylistSimplified[] searchPlaylists(String query) throws ParseException, SpotifyWebApiException, IOException {
        var response = spotify.apiClient()
                .searchPlaylists(query)
                .build()
                .execute();

        return response.getItems();
    }
}
