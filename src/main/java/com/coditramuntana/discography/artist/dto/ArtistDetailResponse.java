package com.coditramuntana.discography.artist.dto;

import com.coditramuntana.discography.artist.Artist;

public record ArtistDetailResponse(Long id, String name, String description, long lpCount) {

    public static ArtistDetailResponse from(Artist artist, long lpCount) {
        return new ArtistDetailResponse(
                artist.getId(),
                artist.getName(),
                artist.getDescription(),
                lpCount
        );
    }
}
