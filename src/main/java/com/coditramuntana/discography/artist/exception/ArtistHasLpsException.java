package com.coditramuntana.discography.artist.exception;

import com.coditramuntana.discography.shared.error.ResourceConflictException;

public class ArtistHasLpsException extends ResourceConflictException {

    private final Long artistId;
    private final long lpCount;

    public ArtistHasLpsException(Long artistId, long lpCount) {
        super(String.format("Cannot delete artist with id %d: has %d associated LP(s)", artistId, lpCount));
        this.artistId = artistId;
        this.lpCount = lpCount;
    }

    public Long getArtistId() {
        return artistId;
    }

    public long getLpCount() {
        return lpCount;
    }
}
