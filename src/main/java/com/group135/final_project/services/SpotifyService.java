package com.group135.final_project.services;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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

        var artists = Arrays.stream(items)
                .map(PlaylistTrack::getTrack)
                .filter(track -> track instanceof Track)
                .map(item -> (Track) item)
                .flatMap(track -> Arrays.stream(track.getArtists()))
                .collect(Collectors.toList());
        
        // the ArtistSimplified class unfortunately does not implement hashcode or equals...
        var ids = new HashSet<String>();
        var uniqueArtists = new ArrayList<ArtistSimplified>();
        
        for (var artist : artists) {
            if (!ids.contains(artist.getId())) {
                ids.add(artist.getId());
                uniqueArtists.add(artist);
            }
        }
        
        return uniqueArtists;
    }

    public PlaylistSimplified[] searchPlaylists(String query) throws ParseException, SpotifyWebApiException, IOException {
        var response = spotify.apiClient()
                .searchPlaylists(query)
                .build()
                .execute();

        return response.getItems();
    }
}
