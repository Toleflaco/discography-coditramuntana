package com.coditramuntana.discography.artist.dto;

import com.coditramuntana.discography.artist.Artist;

public record ArtistResponse(Long id, String name, String description) {

    public static ArtistResponse from(Artist artist) {
        return new ArtistResponse(
                artist.getId(),
                artist.getName(),
                artist.getDescription()
        );
    }
}
