package com.group135.final_project.services;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.springframework.stereotype.Service;

/**
 * Responsible for fetching artist metadata from <a href="https://wikidata.org">wikidata</a>.
 */
@Service
public class LODService {
    
    private static final String WIKIDATA_SPARQL_ENDPOINT = "https://query.wikidata.org/sparql";
    
    private static final String UNFINISHED_QUERY = """ 
    PREFIX wdt: <http://www.wikidata.org/prop/direct/>
    SELECT distinct ?spotifyId ?artistIso ?citizenIso WHERE {
        ?artist wdt:P1902 $spotifyId
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

    /**
     * Fetch artist info from WikiData using the artist's Spotify ID
     */
    public static ResultSet fetchArtistInfo(Collection<String> spotifyIDs) {
      // Enclose all ID's in quotation marks
      spotifyIDs = spotifyIDs.stream().map(s -> '"' + s + '"').collect(Collectors.toList());
      String query = String.format(UNFINISHED_QUERY, String.join(",", spotifyIDs));
      return fetchWikiData(query);
    }
}
