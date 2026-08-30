package com.coditramuntana.discography.lp.dto;

import com.coditramuntana.discography.lp.Lp;

public record LpResponse(
        Long id,
        String name,
        String description,
        String artistName
) {
    public static LpResponse from (Lp lp) {
        return new LpResponse(
                lp.getId(),
                lp.getName(),
                lp.getDescription(),
                lp.getArtist().getName()
        );
    }
}
