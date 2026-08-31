package com.coditramuntana.discography.artist;

import com.coditramuntana.discography.artist.dto.ArtistCreateRequest;
import com.coditramuntana.discography.artist.dto.ArtistDetailResponse;
import com.coditramuntana.discography.artist.dto.ArtistResponse;
import com.coditramuntana.discography.artist.dto.ArtistUpdateRequest;
import com.coditramuntana.discography.artist.exception.ArtistHasLpsException;
import com.coditramuntana.discography.artist.exception.ArtistNameAlreadyExistsException;
import com.coditramuntana.discography.artist.exception.ArtistNotFoundException;
import com.coditramuntana.discography.lp.LpRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final LpRepository lpRepository;

    public ArtistService(ArtistRepository artistRepository, LpRepository lpRepository) {
        this.artistRepository = artistRepository;
        this.lpRepository = lpRepository;
    }

    public Page<ArtistResponse> findAll(Pageable pageable) {
        Pageable effective = pageable.getSort().isUnsorted()
                ? PageRequest.of(pageable.getPageNumber(),pageable.getPageSize(), Sort.by("name"))
                : pageable;

        return artistRepository.findAll(effective).map(ArtistResponse::from);
    }

    public Artist findById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));
    }

    public ArtistDetailResponse findDetailById(Long id) {
        Artist artist = findById(id);
        long lpCount = lpRepository.countByArtistId(id);
        return ArtistDetailResponse.from(artist, lpCount);
    }

    @Transactional
    public ArtistResponse create(ArtistCreateRequest request) {
        if (artistRepository.findByName(request.name()).isPresent()) {
            throw new ArtistNameAlreadyExistsException(request.name());
        }
        Artist artist = new Artist();
        artist.setName(request.name());
        artist.setDescription(request.description());
        Artist saved = artistRepository.save(artist);
        return ArtistResponse.from(saved);
    }

    @Transactional
    public ArtistResponse update(Long id, ArtistUpdateRequest request) {
        Artist artist = findById(id);
        if (!artist.getName().equals(request.name())
                && artistRepository.findByName(request.name()).isPresent()) {
            throw new ArtistNameAlreadyExistsException(request.name());
        }
        artist.setName(request.name());
        artist.setDescription(request.description());
        Artist saved = artistRepository.save(artist);
        return ArtistResponse.from(saved);
    }

    @Transactional
    public void delete(Long id) {
        Artist artist = findById(id);
        long lpCount = lpRepository.countByArtistId(id);
        if (lpCount > 0) {
            throw new ArtistHasLpsException(id, lpCount);
        }
        artistRepository.delete(artist);
    }
}
