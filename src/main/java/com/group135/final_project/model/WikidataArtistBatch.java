package com.group135.final_project.model;

import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class WikidataArtistBatch {
    private final Set<String> resolvedIds = new HashSet<>();
    private final Map<String, ArtistSimplified> artistsToEnhanceById;
    private final List<Pair<ArtistSimplified, String>> enhancedArtists = new ArrayList<>();

    public WikidataArtistBatch(List<ArtistSimplified> artists) {
        artistsToEnhanceById = artistsById(artists);
    }

    public static Map<String, ArtistSimplified> artistsById(List<ArtistSimplified> artists) {
        var artistsById = new HashMap<String, ArtistSimplified>();

        for (var artist : artists) {
            artistsById.put(artist.getId(), artist);
        }

        return artistsById;
    }

    public static Map<String, ArtistSimplified> artistsByName(List<ArtistSimplified> artists) {
        var artistsById = new HashMap<String, ArtistSimplified>();

        for (var artist : artists) {
            artistsById.put(artist.getName(), artist);
        }

        return artistsById;
    }

    public void enhance(ArtistSimplified artist, String wikidataId) {
        enhancedArtists.add(new ImmutablePair<>(artist, wikidataId));
        artistsToEnhanceById.remove(artist.getId());
    }

    public List<ArtistSimplified> artistsToBeEnhanced() {
        return new ArrayList<>(artistsToEnhanceById.values());
    }

    public List<Pair<ArtistSimplified, String>> getEnhancedArtists() {
        return enhancedArtists;
    }
}
