package com.group135.final_project.services;

import java.util.*;
import java.util.stream.Collectors;

import com.group135.final_project.model.EnhancedArtist;
import com.group135.final_project.model.LODArtistInfo;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.springframework.stereotype.Service;

/**
 * Responsible for fetching artist metadata from <a href="https://wikidata.org">wikidata</a>.
 */
@Service
public class LODService {
    
    private static final String WIKIDATA_SPARQL_ENDPOINT = "https://query.wikidata.org/sparql";
    
    private static final String QUERY_TEMPLATE = """ 
    PREFIX wdt: <http://www.wikidata.org/prop/direct/>
    SELECT distinct ?spotifyId ?artistIso ?citizenIso WHERE {
        ?artist wdt:P1902 ?spotifyId
        FILTER(?spotifyId in (%s))
        OPTIONAL {
          ?artist wdt:P495 ?country .
          ?country wdt:P297 ?artistIso .
        }
        OPTIONAL {
          ?artist wdt:P27 ?citizenCountry .
          ?citizenCountry wdt:P297 ?citizenIso .
        }
      }
    """;


    /**
     * Given a SPARQL query, executes it at a the SPARQL ENDPOINT of WikiData
     */
    public static ResultSet fetchWikiData(String query) {
        var queryExecution =  QueryExecutionFactory.sparqlService(WIKIDATA_SPARQL_ENDPOINT, query);
        return queryExecution.execSelect();
    }

    public static ArrayList<EnhancedArtist> enhance(List<Pair<ArtistSimplified, String>> resolvedArtists) {
        var wikiDataIds = resolvedArtists.stream()
                .map(Pair::getRight)
                .map(id -> "wd:" + id)
                .collect(Collectors.joining(" "));
        var query = """ 
            PREFIX wd: <http://www.wikidata.org/entity/>
            PREFIX wdt: <http://www.wikidata.org/prop/direct/>
            SELECT distinct ?artist ?artistIso ?citizenIso WHERE {
                VALUES ?artist { ARTISTS }
                OPTIONAL {
                  ?artist wdt:P495 ?country .
                  ?country wdt:P297 ?artistIso .
                }
                OPTIONAL {
                  ?artist wdt:P27 ?citizenCountry .
                  ?citizenCountry wdt:P297 ?citizenIso .
                }
              }
        """.replace("ARTISTS", wikiDataIds);

        var result = fetchWikiData(query);
        var resolvedArtistsByWikiDataId = new HashMap<String, ArtistSimplified>();
        for (var pair : resolvedArtists) {
            resolvedArtistsByWikiDataId.put(pair.getRight(), pair.getLeft());
        }

        var enhancedArtists = new ArrayList<EnhancedArtist>();

        while (result.hasNext()) {
            var row = result.next();

            var wikiDataId = row.get("artist").asResource().getLocalName();

            var simpleArtist = resolvedArtistsByWikiDataId.get(wikiDataId);
            var countryCodes = new HashSet<String>();

            for (var iso : Arrays.asList(row.get("artistIso"), row.get("citizenIso"))) {
                if (iso != null) countryCodes.add(iso.asLiteral().getString());
            }

            enhancedArtists.add(new EnhancedArtist(simpleArtist, new LODArtistInfo(countryCodes)));
        }

        return enhancedArtists;
    }
}
