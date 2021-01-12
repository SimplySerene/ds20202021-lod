package com.group135.final_project;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;


/**
 * Build an N-triple dataset.
 */
public class BuildNTDataset {
    
    private static final String playlistId = "70HWV2IYmIlGC0iiVNpJmK";
    private static final String owl = "http://www.w3.org/2002/07/owl#";
    private static final String dbp = "http://dbpedia.org/resource/";

    public static void main(String[] args) throws Exception {
        
        Playlist playlist = SpotifyAPI.retrievePlaylist(playlistId);

        /** 
         * Create a new file
         */
        String filename = playlist.getName();
        File file = new File(filename + ".nt");
        while ( !file.createNewFile() ) {
            filename = filename + "x";
            file = new File(filename + ".nt" );
        } 
        Writer writer = new FileWriter(file);

        /**
         * Get all track id's and request the corresponding songs.
         * Playlist responses do not include artist name.
         */
        Set<String> trackIds = new HashSet<>();
        for (PlaylistTrack track: playlist.getTracks().getItems()) {
            trackIds.add(track.getTrack().getId());
        }
        Track[] tracks = SpotifyAPI.retrieveTracksWithID(trackIds);

        /**
         * Get all artists
         */
        Set<ArtistSimplified> artists = new HashSet<>();
        for (Track track: tracks) {
            if (track != null) {
                for (ArtistSimplified artist: track.getArtists()) {
                    artists.add(artist);
                }
            }
        }

        /**
         * Write triples to file
         */
        String predicate = "<" + owl + "sameAs>";
        for (ArtistSimplified artist: artists) {
            String subject, object;
            subject = "subject";
            object = dbp + artist.getName();
            String triple = subject + " " + predicate + " " + object + " .\n";
            writer.write(triple);
        }

        writer.close();
    }
}