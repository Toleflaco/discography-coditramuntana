package com.coditramuntana.discography.artist;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ArtistRepositoryTest {

    @Autowired
    private ArtistRepository artistRepository;

    @Test
    void buscaNombreParcialSinDistinguirMayusculasYMinusculasYPaginaResultados() {
        artistRepository.save(new Artist("The Mixed Case Band", "description"));
        artistRepository.save(new Artist("Another Mixed Case Band", "description"));
        artistRepository.save(new Artist("Unrelated Artist", "description"));
        artistRepository.flush();

        Page<Artist> artists = artistRepository.findByNameContainingIgnoreCase(
                "mIxEd cAsE", PageRequest.of(0, 1, Sort.by("name"))
        );

        assertThat(artists.getTotalElements()).isEqualTo(2);
        assertThat(artists.getContent()).hasSize(1);
        assertThat(artists.getContent().getFirst().getName()).isEqualTo("Another Mixed Case Band");
    }
}
