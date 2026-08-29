package com.coditramuntana.discography.artist;

import com.coditramuntana.discography.artist.dto.ArtistResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("/{id}")
    public ArtistResponse findById(@PathVariable Long id) {
        Artist artist = artistService.findById(id);
        return ArtistResponse.from(artist);
    }
}
