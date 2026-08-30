package com.coditramuntana.discography.lp;

import com.coditramuntana.discography.artist.Artist;
import com.coditramuntana.discography.artist.ArtistRepository;
import com.coditramuntana.discography.artist.exception.ArtistNotFoundException;
import com.coditramuntana.discography.lp.dto.LpCreateRequest;
import com.coditramuntana.discography.lp.dto.LpDetailResponse;
import com.coditramuntana.discography.lp.dto.LpResponse;
import com.coditramuntana.discography.lp.dto.LpUpdateRequest;
import com.coditramuntana.discography.lp.exception.LpAlreadyExistsForArtistException;
import com.coditramuntana.discography.lp.exception.LpNotFoundException;
import com.coditramuntana.discography.song.SongRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LpService {
    private final LpRepository lpRepository;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;


    public LpService(LpRepository lpRepository, SongRepository songRepository, ArtistRepository artistRepository) {
        this.lpRepository = lpRepository;
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
    }

    public Page<LpResponse> findAll(String artistName, Pageable pageable) {
        Page<Lp> lps = (artistName == null || artistName.isEmpty())
                ? lpRepository.findAllWithArtist(pageable)
                : lpRepository.findAllByArtistNameWithArtist(artistName, pageable);
        return lps.map(LpResponse::from);
    }

    public LpDetailResponse findDetailById(Long id) {
        Lp lp = findLpById(id);
        long songCount = songRepository.countByLpId(id);
        return LpDetailResponse.from(lp, songCount);
    }


    @Transactional
    public LpResponse create(LpCreateRequest request) {
        Artist artist = artistRepository.findById(request.artistId())
                .orElseThrow(() -> new ArtistNotFoundException(request.artistId()));
        if (lpRepository.findByArtistIdAndName(request.artistId(), request.name()).isPresent()) {
            throw new LpAlreadyExistsForArtistException(request.artistId(), request.name());
        }
        Lp lp = new Lp();
        lp.setDescription(request.description());
        lp.setName(request.name());
        lp.setArtist(artist);
        Lp saved = lpRepository.save(lp);
        return LpResponse.from(saved);
    }


    @Transactional
    public LpResponse update(Long id, LpUpdateRequest request) {
        Lp lp = findLpById(id);
        if (!request.name().equals(lp.getName())) {
            if (lpRepository.findByArtistIdAndName(lp.getArtist().getId(), request.name()).isPresent()) {
                throw new LpAlreadyExistsForArtistException(lp.getArtist().getId(), request.name());
            }
        }
        lp.setName(request.name());
        lp.setDescription(request.description());
        Lp saved = lpRepository.save(lp);
        return LpResponse.from(saved);

    }

    @Transactional
    public void delete(Long id) {
        Lp lp = findLpById(id);
        lpRepository.delete(lp);
    }

    private Lp findLpById(Long id) {
        return lpRepository.findByIdWithArtist(id)
                .orElseThrow(() -> new LpNotFoundException(id));
    }
}
