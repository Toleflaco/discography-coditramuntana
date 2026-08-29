package com.coditramuntana.discography.artist.exception;

import com.coditramuntana.discography.shared.error.ResourceConflictException;

public class ArtistNameAlreadyExistsException extends ResourceConflictException {

    private final String name;

    public ArtistNameAlreadyExistsException(String name) {
        super(String.format("Artist with name '%s' already exists", name));
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
