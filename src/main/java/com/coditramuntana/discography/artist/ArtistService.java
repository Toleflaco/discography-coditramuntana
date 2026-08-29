package com.coditramuntana.discography.artist;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public Artist findById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));
    }
}
