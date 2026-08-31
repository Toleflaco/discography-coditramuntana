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
public class LpServiceTest {

    @Mock
    private LpRepository lpRepository;

    @Mock
    private SongRepository songRepository;

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private LpService lpService;


    @Nested
    class FindAll {

        @Test
        void aplicaSortPorDefectoCuandoPageableLlegaSinSort() {
            // ---------- ARRANGE ----------
            when(lpRepository.findAllWithArtist(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of()));

            // Pageable SIN sort (el cliente no mandó ?sort=)
            Pageable pageableSinSort = PageRequest.of(0, 10);

            // ---------- ACT ----------
            lpService.findAll(null, pageableSinSort);

            // ---------- ASSERT ----------
            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(lpRepository).findAllWithArtist(captor.capture());

            Sort sortAplicado = captor.getValue().getSort();

            assertThat(sortAplicado).isEqualTo(Sort.by("artist.name", "name"));
        }

        @Test
        void respetaSortDelClienteCuandoPageableLlegaConSort() {
            // ---------- ARRANGE ----------
            when(lpRepository.findAllWithArtist(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of()));

            // Pageable CON sort explícito del cliente
            Sort sortDelCliente = Sort.by("name").descending();
            Pageable pageableConSort = PageRequest.of(0, 10, sortDelCliente);

            // ---------- ACT ----------
            lpService.findAll(null, pageableConSort);

            // ---------- ASSERT ----------
            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(lpRepository).findAllWithArtist(captor.capture());

            Sort sortAplicado = captor.getValue().getSort();

            assertThat(sortAplicado).isEqualTo(sortDelCliente);
        }
    }

    @Nested
    class FindDetailById {

        @Test
        void lanzaLpNotFoundExceptionCuandoIdNoExiste() {
            // ---------- ARRANGE ----------
            when(lpRepository.findByIdWithArtist(99L))
                    .thenReturn(Optional.empty());

            // ---------- ACT + ASSERT ----------
            assertThatThrownBy(() -> lpService.findDetailById(99L))
                    .isInstanceOf(LpNotFoundException.class);

            verifyNoInteractions(songRepository);
        }

        @Test
        void devuelveDtoConSongCountCuandoLpExiste() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test Band", "description");
            Lp lp = new Lp("Test Lp", "description");
            lp.setArtist(artist);

            when(lpRepository.findByIdWithArtist(1L))
                    .thenReturn(Optional.of(lp));
            when(songRepository.countByLpId(1L))
                    .thenReturn(5L);

            // ---------- ACT ----------
            LpDetailResponse response = lpService.findDetailById(1L);

            // ---------- ASSERT ----------
            assertThat(response.name()).isEqualTo("Test Lp");
            assertThat(response.songCount()).isEqualTo(5L);
        }
    }

    @Nested
    class Create {

        @Test
        void lanzaArtistNotFoundExceptionCuandoArtistIdNoExiste() {
            // ---------- ARRANGE ----------
            LpCreateRequest request = new LpCreateRequest("Test Lp", "description", 99L);
            when(artistRepository.findById(99L))
                    .thenReturn(Optional.empty());

            // ---------- ACT + ASSERT ----------
            assertThatThrownBy(() -> lpService.create(request))
                    .isInstanceOf(ArtistNotFoundException.class);

            verifyNoInteractions(lpRepository);
        }

        @Test
        void lanzaLpAlreadyExistsForArtistExceptionCuandoNameYaExisteParaArtist() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test Band", "description");
            Lp lpExistente = new Lp("Repetido", "otra description");
            LpCreateRequest request = new LpCreateRequest("Repetido", "description", 1L);

            when(artistRepository.findById(1L))
                    .thenReturn(Optional.of(artist));
            when(lpRepository.findByArtistIdAndName(1L, "Repetido"))
                    .thenReturn(Optional.of(lpExistente));

            // ---------- ACT + ASSERT ----------
            assertThatThrownBy(() -> lpService.create(request))
                    .isInstanceOf(LpAlreadyExistsForArtistException.class);

            verify(lpRepository, never()).save(any());
        }

        @Test
        void persisteConArtistAsignadoCuandoNoHayColision() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test Band", "description");
            LpCreateRequest request = new LpCreateRequest("Nuevo Lp", "description", 1L);

            when(artistRepository.findById(1L))
                    .thenReturn(Optional.of(artist));
            when(lpRepository.findByArtistIdAndName(1L, "Nuevo Lp"))
                    .thenReturn(Optional.empty());
            when(lpRepository.save(any(Lp.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // ---------- ACT ----------
            LpResponse response = lpService.create(request);

            // ---------- ASSERT ----------
            ArgumentCaptor<Lp> captor = ArgumentCaptor.forClass(Lp.class);
            verify(lpRepository).save(captor.capture());

            assertThat(captor.getValue().getArtist()).isSameAs(artist);
            assertThat(response.name()).isEqualTo("Nuevo Lp");
        }
    }

    @Nested
    class Update {

        @Test
        void lanzaLpNotFoundExceptionCuandoIdNoExiste() {
            // ---------- ARRANGE ----------
            LpUpdateRequest request = new LpUpdateRequest("Nuevo nombre", "description");
            when(lpRepository.findByIdWithArtist(99L))
                    .thenReturn(Optional.empty());

            // ---------- ACT + ASSERT ----------
            assertThatThrownBy(() -> lpService.update(99L, request))
                    .isInstanceOf(LpNotFoundException.class);

            verify(lpRepository, never()).save(any());
        }

        @Test
        void noCompruebaColisionCuandoNombreNoCambia() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test Band", "description");
            Lp lp = new Lp("Mismo nombre", "description antigua");
            lp.setArtist(artist);
            LpUpdateRequest request = new LpUpdateRequest("Mismo nombre", "description nueva");

            when(lpRepository.findByIdWithArtist(1L))
                    .thenReturn(Optional.of(lp));
            when(lpRepository.save(any(Lp.class)))
                    .thenReturn(lp);

            // ---------- ACT ----------
            LpResponse response = lpService.update(1L, request);

            // ---------- ASSERT ----------
            verify(lpRepository, never()).findByArtistIdAndName(any(), any());
            assertThat(response.description()).isEqualTo("description nueva");
        }

        @Test
        void lanzaLpAlreadyExistsForArtistExceptionCuandoNombreCambiaYColisiona() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test Band", "description");
            Lp lp = new Lp("Original", "description");
            lp.setArtist(artist);
            Lp lpColision = new Lp("Nuevo nombre", "otra description");
            LpUpdateRequest request = new LpUpdateRequest("Nuevo nombre", "description");

            when(lpRepository.findByIdWithArtist(1L))
                    .thenReturn(Optional.of(lp));
            when(lpRepository.findByArtistIdAndName(any(), eq("Nuevo nombre")))
                    .thenReturn(Optional.of(lpColision));

            // ---------- ACT + ASSERT ----------
            assertThatThrownBy(() -> lpService.update(1L, request))
                    .isInstanceOf(LpAlreadyExistsForArtistException.class);

            verify(lpRepository, never()).save(any());
        }

        @Test
        void persisteCambioCuandoNoHayColision() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test Band", "description");
            Lp lp = new Lp("Original", "description");
            lp.setArtist(artist);
            LpUpdateRequest request = new LpUpdateRequest("Nuevo nombre", "description nueva");

            when(lpRepository.findByIdWithArtist(1L))
                    .thenReturn(Optional.of(lp));
            when(lpRepository.findByArtistIdAndName(any(), eq("Nuevo nombre")))
                    .thenReturn(Optional.empty());
            when(lpRepository.save(any(Lp.class)))
                    .thenReturn(lp);

            // ---------- ACT ----------
            LpResponse response = lpService.update(1L, request);

            // ---------- ASSERT ----------
            ArgumentCaptor<Lp> captor = ArgumentCaptor.forClass(Lp.class);
            verify(lpRepository).save(captor.capture());
            assertThat(captor.getValue().getName()).isEqualTo("Nuevo nombre");
            assertThat(response.name()).isEqualTo("Nuevo nombre");
        }
    }

    @Nested
    class Delete {

        @Test
        void lanzaLpNotFoundExceptionCuandoIdNoExiste() {
            // ---------- ARRANGE ----------
            when(lpRepository.findByIdWithArtist(99L))
                    .thenReturn(Optional.empty());

            // ---------- ACT + ASSERT ----------
            assertThatThrownBy(() -> lpService.delete(99L))
                    .isInstanceOf(LpNotFoundException.class);

            verify(lpRepository, never()).delete(any());
        }

        @Test
        void persisteBorradoCuandoLpExiste() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test Band", "description");
            Lp lp = new Lp("Test Lp", "description");
            lp.setArtist(artist);

            when(lpRepository.findByIdWithArtist(1L))
                    .thenReturn(Optional.of(lp));

            // ---------- ACT ----------
            lpService.delete(1L);

            // ---------- ASSERT ----------
            verify(lpRepository).delete(lp);
        }
    }
}
