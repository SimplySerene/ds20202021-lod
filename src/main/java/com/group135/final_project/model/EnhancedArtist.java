package com.group135.final_project.model;

import com.wrapper.spotify.model_objects.specification.ArtistSimplified;

public class EnhancedArtist {
    public ArtistSimplified spotifyArtist;
    public LODArtistInfo lodInfo;

    public EnhancedArtist(ArtistSimplified spotifyArtist, LODArtistInfo lodInfo) {
        this.spotifyArtist = spotifyArtist;
        this.lodInfo = lodInfo;
    }
}
