package com.coditramuntana.discography.artist;

import com.coditramuntana.discography.artist.dto.ArtistCreateRequest;
import com.coditramuntana.discography.artist.dto.ArtistDetailResponse;
import com.coditramuntana.discography.artist.dto.ArtistResponse;
import com.coditramuntana.discography.artist.dto.ArtistUpdateRequest;
import com.coditramuntana.discography.artist.exception.ArtistHasLpsException;
import com.coditramuntana.discography.artist.exception.ArtistNameAlreadyExistsException;
import com.coditramuntana.discography.artist.exception.ArtistNotFoundException;
import com.coditramuntana.discography.lp.LpRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceTest {

    @Mock
    private LpRepository lpRepository;

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistService artistService;


    @Nested
    class Delete {
        @Test
        void compruebaArtistNotFound() {
            // ---------- ARRANGE ----------
            when(artistRepository.findById(99L))
                    .thenReturn(Optional.empty());

            // ---------- ACT + ASSERT ----------
            assertThatThrownBy(() -> artistService.delete(99L))
                    .isInstanceOf(ArtistNotFoundException.class);

            // El artist no existe: nunca debe llegar al lpRepository
            verifyNoInteractions(lpRepository);
        }

        @Test
        void compruebaArtistHasLpsException() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test band", "description");

            when(artistRepository.findById(1L))
                    .thenReturn(Optional.of(artist));
            when(lpRepository.countByArtistId(1L))
                    .thenReturn(1L);

            // ---------- ACT + ASSERT ----------
            assertThatThrownBy(() -> artistService.delete(1L))
                    .isInstanceOf(ArtistHasLpsException.class);

        }

        @Test
        void persisteBorradoCuandoNoTieneLps() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test band", "description");
            when(artistRepository.findById(1L))
                    .thenReturn(Optional.of(artist));
            when(lpRepository.countByArtistId(1L))
                    .thenReturn(0L);

            // ---------- ACT ------------
            artistService.delete(1L);

            // ---------- ASSERT ----------
            verify(artistRepository).delete(artist);
        }
    }

    @Nested
    class Update {

        @Test
        void lanzaArtistNotFoundExceptionCuandoIdNoExiste() {
            // ---------- ARRANGE ----------
            ArtistUpdateRequest request = new ArtistUpdateRequest("Test Band", "description");
            when(artistRepository.findById(99L))
                    .thenReturn(Optional.empty());

            // ---------- ACT + ASSERT ------------
            assertThatThrownBy(() -> artistService.update(99L, request))
                    .isInstanceOf(ArtistNotFoundException.class);
            verifyNoInteractions(lpRepository);
        }

        @Test
        void lanzaArtistNameAlreadyExistsExceptionCuandoNuevoNameColisiona() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Original", "description");
            Artist otherArtist = new Artist("YaEnBD", "description");
            ArtistUpdateRequest request = new ArtistUpdateRequest("Solicitado", "desc");

            when(artistRepository.findById(1L))
                    .thenReturn(Optional.of(artist));
            when(artistRepository.findByName("Solicitado"))
                    .thenReturn(Optional.of(otherArtist));
            // ---------- ACT + ASSERT ------------
            assertThatThrownBy(() -> artistService.update(1L, request))
                    .isInstanceOf(ArtistNameAlreadyExistsException.class);
            verify(artistRepository, never()).save(any());


        }

        @Test
        void actualizaYDevuelveDtoCuandoNoHayColision() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Original", "description");
            ArtistUpdateRequest request = new ArtistUpdateRequest("Nuevo", "description");

            when(artistRepository.findById(1L))
                    .thenReturn(Optional.of(artist));
            when(artistRepository.findByName("Nuevo"))
                    .thenReturn(Optional.empty());
            when(artistRepository.save(any(Artist.class)))
                    .thenReturn(artist);

            // ---------- ACT ------------------
            ArtistResponse response = artistService.update(1L, request);


            // ---------  ASSERT ------------
            verify(artistRepository).save(artist);
            assertThat(response.name()).isEqualTo("Nuevo");
        }

    }

    @Nested
    class Create {

        @Test
        void lanzaArtistNameAlreadyExistsExceptionCuandoNameYaExiste() {

            // ---------- ARRANGE ----------
            Artist artist = new Artist("Ya estoy", "description");
            ArtistCreateRequest request = new ArtistCreateRequest("Ya estoy", "description");
            when(artistRepository.findByName("Ya estoy"))
                    .thenReturn(Optional.of(artist));

            // ---------- ACT + ASSERT ------------
            assertThatThrownBy(() -> artistService.create(request))
                    .isInstanceOf(ArtistNameAlreadyExistsException.class);
            verify(artistRepository, never()).save(any());
        }

        @Test
        void persisteYDevuelveDtoCuandoNoHayColision() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Nuevo", "description");
            ArtistCreateRequest request = new ArtistCreateRequest("Nuevo", "description");
            when(artistRepository.findByName("Nuevo"))
                    .thenReturn(Optional.empty());
            when(artistRepository.save(any(Artist.class)))
                    .thenReturn(artist);

            // ---------- ACT ------------------
            ArtistResponse response = artistService.create(request);


            // ---------  ASSERT ------------
            verify(artistRepository).save(any(Artist.class));
            assertThat(response.name()).isEqualTo("Nuevo");

        }
    }

    @Nested
    class FindDetailById {

        @Test
        void lanzaArtistNotFoundExceptionCuandoIdNoExiste() {
            // ---------- ARRANGE ----------
            when(artistRepository.findById(99L))
                    .thenReturn(Optional.empty());

            // ---------- ACT + ASSERT ------------------
            assertThatThrownBy(() -> artistService.findDetailById(99L))
                    .isInstanceOf(ArtistNotFoundException.class);
            verifyNoInteractions(lpRepository);
        }


        @Test
        void devuelveDetailConLpCountCuandoArtistExiste() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test Band", "description");
            when(artistRepository.findById(1L))
                    .thenReturn(Optional.of(artist));
            when(lpRepository.countByArtistId(1L))
                    .thenReturn(5L);

            // --------- ACT ----------
            ArtistDetailResponse response = artistService.findDetailById(1L);


            // ---------- ASSERT ----------
            assertThat(response.name()).isEqualTo(artist.getName());
            assertThat(response.lpCount()).isEqualTo(5L);

        }
    }

    @Nested
    class FindAll {
        @Test
        void aplicaSortPorDefectoCuandoPageableLlegaSinSort() {
            // ---------- ARRANGE ----------

            when(artistRepository.findAll(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of()));

            // Pageable SIN sort (el cliente no mandó ?sort=)
            Pageable pageableSinSort = PageRequest.of(0, 10);

            // --------- ACT ----------

            artistService.findAll(pageableSinSort);
            // ---------- ASSERT ----------

            // Capturamos el Pageable que el service pasó al repository
            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(artistRepository).findAll(captor.capture());

            // Sacamos el Pageable capturado y verificamos su sort
            Sort sortAplicado = captor.getValue().getSort();

            assertThat(sortAplicado).isEqualTo(Sort.by("name"));
        }
    }
}


