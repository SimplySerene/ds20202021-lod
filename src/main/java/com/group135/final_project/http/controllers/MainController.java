package com.group135.final_project.http.controllers;

import com.group135.final_project.model.EnhancedArtist;
import com.group135.final_project.services.LODService;
import com.group135.final_project.services.SpotifyService;
import com.group135.final_project.services.WikidataIdResolver;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import org.apache.hc.core5.http.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

/**
 * The touch point between our frontend and this API.
 */
@Controller
@RequestMapping("/api")
public class MainController {
    private final SpotifyService spotifyService;

    public MainController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    /**
     * Returns enriched data from WikiData and Spotify.
     */
    @GetMapping("/playlist-info/{playlistId}")
    public ResponseEntity<List<EnhancedArtist>> playlistInfo(
            @PathVariable String playlistId
    ) throws ParseException, SpotifyWebApiException, IOException {
        var simpleArtists = spotifyService.fetchArtistsFromPlaylist(playlistId);
        var resolvedArtists = WikidataIdResolver.resolveWikidataIds(simpleArtists);
        var enhancedArtists = LODService.enhance(resolvedArtists);

        return ResponseEntity.ok(enhancedArtists);
    }

    /**
     * Exposes the Spotify playlist search.
     */
    @GetMapping("/playlist-search")
    public ResponseEntity<PlaylistSimplified[]> playlistSearch(
            @RequestParam String query
    ) throws ParseException, SpotifyWebApiException, IOException {
        var results = spotifyService.searchPlaylists(query);

        return ResponseEntity.ok(results);
    }
}
