package com.coditramuntana.discography.lp.exception;

import com.coditramuntana.discography.shared.error.ResourceConflictException;

public class LpAlreadyExistsForArtistException extends ResourceConflictException {

    private final String name;

    public LpAlreadyExistsForArtistException(Long artistId, String name) {
        super(String.format("Lp with name '%s' already exists in Artist '%d'", name, artistId));
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
