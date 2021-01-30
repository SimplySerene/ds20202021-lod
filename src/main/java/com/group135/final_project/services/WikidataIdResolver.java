package com.group135.final_project.services;

import com.group135.final_project.model.WikidataArtistBatch;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class tries to find a wikidata entry for each of our artists.
 */
public class WikidataIdResolver {
    public static final Logger log = LoggerFactory.getLogger(WikidataIdResolver.class);

    public static List<Pair<ArtistSimplified, String>> resolveWikidataIds(List<ArtistSimplified> artists) {
        var batch = new WikidataArtistBatch(artists);
        var resolvedById = 0;
        var resolvedByName = 0;

        // We first try to use the spotify-ids, as these are precise and fast to retrieve
        // from wikidata
        for (var pair : resolveBySpotifyId(batch.artistsToBeEnhanced())) {
            resolvedById++;
            batch.enhance(pair.getLeft(), pair.getRight());
        }

        // The remaining artists are looked up based on their name, which is more expensive,
        // but helps with e.g. the "French Touch" playlist, where only 17 artists have
        // spotify-ids in wikidata, but 31 can be resolved by name!
        for (var pair : resolveByName(batch.artistsToBeEnhanced())) {
            resolvedByName++;
            batch.enhance(pair.getLeft(), pair.getRight());
        }

        var resolvedTotal = resolvedById + resolvedByName;
        log.info("Resolved " + resolvedTotal + "/" + artists.size() +  " (" + resolvedById + " by id and " + resolvedByName + " by name)!");

        return batch.getEnhancedArtists();
    }

    private static List<Pair<ArtistSimplified, String>> resolveBySpotifyId(List<ArtistSimplified> artists) {
        var artistsById = WikidataArtistBatch.artistsById(artists);
        var spotifyIds = artists.stream()
                .map(ArtistSimplified::getId)
                .map(id -> "\"" + id + "\"")
                .collect(Collectors.joining(", "));

        var result = LODService.fetchWikiData("""
                PREFIX wdt: <http://www.wikidata.org/prop/direct/>
                SELECT distinct ?artist ?spotifyId 
                WHERE {
                    ?artist wdt:P1902 ?spotifyId
                    FILTER(?spotifyId in (SPOTIFY_IDS))
                }
                """.replace("SPOTIFY_IDS", spotifyIds)
        );

        var resolvedPairs = new ArrayList<Pair<ArtistSimplified, String>>();

        while (result.hasNext()) {
            var row = result.next();
            var spotifyId = row.get("spotifyId").asLiteral().getString();
            var wikiDataId = row.get("artist").asResource().getLocalName();
            var artist = artistsById.get(spotifyId);

            resolvedPairs.add(new ImmutablePair<>(artist, wikiDataId));
        }

        return resolvedPairs;
    }

    private static List<Pair<ArtistSimplified, String>> resolveByName(List<ArtistSimplified> artists) {
        var searchBlocks = artists.stream()
                .map(ArtistSimplified::getName)
                .map(name ->
                        """
                        {
                          BIND("ARTIST_NAME" as ?query)
                          # We search for an artist
                          SERVICE wikibase:mwapi {
                            bd:serviceParam wikibase:endpoint "www.wikidata.org";
                                            wikibase:api "Generator";
                                            wikibase:limit "10";
                                            mwapi:generator "search";
                                            mwapi:gsrsearch "inlabel:ARTIST_NAME".
                              ?item wikibase:apiOutputItem mwapi:title .
                          }
                        }
                        """.replaceAll("ARTIST_NAME", name)
                )
                .collect(Collectors.joining(" UNION "));

        var query = """
        PREFIX wd: <http://www.wikidata.org/entity/>
        PREFIX wikibase: <http://wikiba.se/ontology#>
        PREFIX wdt: <http://www.wikidata.org/prop/direct/>
        PREFIX hint: <http://www.bigdata.com/queryHints#>
        PREFIX bd: <http://www.bigdata.com/rdf#>
        PREFIX mwapi: <https://www.mediawiki.org/ontology#API/>
        
        SELECT DISTINCT ?query ?item
        WHERE
        {
          # SEARCH_BLOCKS
        
          {
            # If the result is a single human
            ?item wdt:P31 wd:Q5.
            # its occupation should be a subclass or instance of musical profession
            ?item wdt:P106/(wdt:P279 | wdt:P31)* wd:Q66715801. hint:Prior hint:gearing "forward".
          } UNION {
            # If the result does not match the first block, we are most likely
            # dealing with a band, as bands are not instanceof human.
            # In this case the result should relate to "musical ensemble"
            ?item wdt:P31/wdt:P279* wd:Q2088357. hint:Prior hint:gearing "forward".
          }
        }    
        """.replace("# SEARCH_BLOCKS", searchBlocks);

        var result = LODService.fetchWikiData(query);

        var artistsByName = WikidataArtistBatch.artistsByName(artists);
        var resolvedPairs = new ArrayList<Pair<ArtistSimplified, String>>();

        while (result.hasNext()) {
            var row = result.next();
            var name = row.get("query").asLiteral().getString();
            var wikiDataId = row.get("item").asResource().getLocalName();
            var artist = artistsByName.get(name);

            resolvedPairs.add(new ImmutablePair<>(artist, wikiDataId));
        }

        return resolvedPairs;
    }
}
