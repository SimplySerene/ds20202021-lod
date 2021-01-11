package com.group135.final_project;

import java.io.IOException;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchArtistsRequest;
/**
 * Fiddling with the Spotify API
 */
public class App 
{
    private static final String clientId = "384237b4e98248f0b1d5ee4f901f6f7c";
    private static final String clientSecret = "9e96c4c77ff64ba881aca2596c02b6ad";

    public static void main( String[] args ) {

        String artist = "dodie";


        SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret).build();   
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        
        try {
            ClientCredentials clientCred = clientCredentialsRequest.execute();
            spotifyApi.setAccessToken(clientCred.getAccessToken());

            System.out.println("Expires in: " + clientCred.getExpiresIn());

            System.out.println("Getting artist: " + artist);
            SearchArtistsRequest artistRequest =  spotifyApi.searchArtists(artist).build();
            Paging<Artist> artistPaging = artistRequest.execute();
            System.out.println(artistPaging.getTotal() + " results found.");
            System.out.println("Results:");
            System.out.println("----------------");
            for (Artist a : artistPaging.getItems()) {
                System.out.println("Name: " + a.getName());
                System.out.println("Id: " + a.getId());
                System.out.println("Followers: " + a.getFollowers().getTotal());
                System.out.println("Popularity:" + a.getPopularity());
                System.out.println("----------------");
            }

        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
            System.out.println(e.getMessage());
        }


    }

}
