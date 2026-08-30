package com.coditramuntana.discography.lp.dto;

import com.coditramuntana.discography.artist.dto.ArtistResponse;
import com.coditramuntana.discography.lp.Lp;

public record LpDetailResponse(
        Long id,
        String name,
        String description,
        ArtistResponse artist,
        long songCount
) {
    public static LpDetailResponse from(Lp lp, long songCount) {
        return new LpDetailResponse(
                lp.getId(),
                lp.getName(),
                lp.getDescription(),
                ArtistResponse.from(lp.getArtist()),
                songCount
        );
    }
}
