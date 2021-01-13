package com.group135.final_project.http.controllers;

import com.group135.final_project.model.EnhancedArtist;
import com.group135.final_project.model.LODArtistInfo;
import com.group135.final_project.services.LODService;
import com.group135.final_project.services.SpotifyService;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import org.apache.hc.core5.http.ParseException;
import org.apache.jena.query.QuerySolution;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

        // Fetch info
        var artistsSpotify = spotifyService.fetchArtistsFromPlaylist(playlistId);
        var artistLOD = LODService.fetchArtistInfo(artistsSpotify.stream().map(ArtistSimplified::getId).collect(Collectors.toList()));

        // Extract Country ISO's
        // Create a mapping from the spotifyID to a set of the CountryISO's.
        Map<String, Set<String>> lodMapping = new HashMap<>();
        
        for (; artistLOD.hasNext() ;) {
            QuerySolution sol = artistLOD.next();
            String spotifyId = sol.get("spotifyId").asLiteral().getString();
            var set = new HashSet<String>();

            // Check if artistIso exsists
            if (sol.get("artistIso") != null) {
                set.add(sol.get("artistIso").asLiteral().getString());
            }

            // Check if citizenIso exsists
            if (sol.get("citizenIso") != null) {
                set.add(sol.get("citizenIso").asLiteral().getString());
            }
            lodMapping.put(spotifyId, set);
        }
        // Create a list of EnhancedArtists by mapping the artistSimplifed to the createEnhancedArtist function
        // together with the just created mapping.
        var enhancedArtists = artistsSpotify.stream().map(
            artistSimpl -> createEnhancedArtist(artistSimpl, lodMapping))
            .collect(Collectors.toList());

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

    // ---------------------
    // Private methods
    // ---------------------
    static private EnhancedArtist createEnhancedArtist(ArtistSimplified artist, Map<String, Set<String>> mapping) {
        var lodArtistInfo = new LODArtistInfo(mapping.get(artist.getId()));
        return new EnhancedArtist(artist, lodArtistInfo);
    }
    
}
