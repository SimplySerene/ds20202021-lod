package com.group135.final_project.model;

import com.wrapper.spotify.model_objects.specification.ArtistSimplified;

import java.util.Objects;

/**
 * This is just a wrapper in order to use Sets with the Artist.
 * Unfortunately, {@link ArtistSimplified} does not implement equals or hashcode,
 * so we cannot use it in sets.
 */
public class IdentifiableArtist {
    public final ArtistSimplified artist;
    private final String spotifyId;

    public IdentifiableArtist(ArtistSimplified artist) {
        this.artist = artist;
        this.spotifyId = artist.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentifiableArtist that = (IdentifiableArtist) o;
        return spotifyId.equals(that.spotifyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spotifyId);
    }
}
