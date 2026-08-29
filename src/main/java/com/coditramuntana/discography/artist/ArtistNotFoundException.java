package com.coditramuntana.discography.artist;

import com.coditramuntana.discography.shared.error.ResourceNotFoundException;

public class ArtistNotFoundException extends ResourceNotFoundException {

    public ArtistNotFoundException(Long id) {
        super("Artist", id);
    }
}
