package com.group135.final_project;

import java.io.IOException;
import java.util.Collection;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import com.wrapper.spotify.requests.data.tracks.GetSeveralTracksRequest;

import org.apache.hc.core5.http.ParseException;

/**
 *  Wraps the spotiy api. Should be merged with SpotifyService.
 */
public class SpotifyAPI {
    
    /**
     * clientId and clientSecret for the Spotify web API, connected to Timme's Spotify.
     */
    private static final String clientId = "384237b4e98248f0b1d5ee4f901f6f7c";
    private static final String clientSecret = "9e96c4c77ff64ba881aca2596c02b6ad";
    /**
     * Spotify API object
     */
    private static SpotifyApi api = new SpotifyApi.Builder()
        .setClientId(clientId)
        .setClientSecret(clientSecret).build();
    

    public static Playlist retrievePlaylist(String playlistId) 
                throws IOException, SpotifyWebApiException, ParseException {
        setupAccessToken();
        GetPlaylistRequest playlistReq = api.getPlaylist(playlistId).build();
        return playlistReq.execute();
    }


    public static Track[] retrieveTracksWithID(Collection<String> ids)
                throws IOException, SpotifyWebApiException, ParseException {   
        setupAccessToken();
        String commaSeperatedList = String.join(",", ids);
        GetSeveralTracksRequest severalTrackRequest = api.getSeveralTracks(commaSeperatedList).build();
        return severalTrackRequest.execute();
    }


    private static void setupAccessToken() 
                throws IOException, SpotifyWebApiException, ParseException {
        if (api.getAccessToken() == null ) {
            ClientCredentials clientCred = api.clientCredentials().build().execute();
            api.setAccessToken(clientCred.getAccessToken());
            System.out.println("Access token expires in: " + clientCred.getExpiresIn());
        } 


            
    }

}
