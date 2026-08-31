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
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;

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
    @DisplayName("findAll(String artistName, Pageable pageable)")
    class FindAll {

        @Test
        @DisplayName("Aplica el sort por defecto cuando el pageable llega sin sort")
        void aplicaSortPorDefectoCuandoPageableLlegaSinSort() {
            // ---------- ARRANGE ----------
            given(lpRepository.findAllWithArtist(any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of()));

            // Pageable SIN sort (el cliente no mandó ?sort=)
            Pageable pageableSinSort = PageRequest.of(0, 10);

            // ---------- ACT ----------
            lpService.findAll(null, pageableSinSort);

            // ---------- ASSERT ----------
            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            then(lpRepository).should().findAllWithArtist(captor.capture());

            Sort sortAplicado = captor.getValue().getSort();

            assertThat(sortAplicado).isEqualTo(Sort.by("artist.name", "name"));
        }

        @Test
        @DisplayName("Respeta el sort del cliente cuando el pageable llega con sort")
        void respetaSortDelClienteCuandoPageableLlegaConSort() {
            // ---------- ARRANGE ----------
            given(lpRepository.findAllWithArtist(any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of()));

            // Pageable CON sort explícito del cliente
            Sort sortDelCliente = Sort.by("name").descending();
            Pageable pageableConSort = PageRequest.of(0, 10, sortDelCliente);

            // ---------- ACT ----------
            lpService.findAll(null, pageableConSort);

            // ---------- ASSERT ----------
            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            then(lpRepository).should().findAllWithArtist(captor.capture());

            Sort sortAplicado = captor.getValue().getSort();

            assertThat(sortAplicado).isEqualTo(sortDelCliente);
        }
    }

    @Nested
    @DisplayName("findDetailById(Long id)")
    class FindDetailById {

        @Test
        @DisplayName("Lanza LpNotFoundException cuando el id no existe")
        void lanzaLpNotFoundExceptionCuandoIdNoExiste() {
            // ---------- ARRANGE ----------
            given(lpRepository.findByIdWithArtist(99L))
                    .willReturn(Optional.empty());

            // ---------- ACT + ASSERT ----------
            assertThatThrownBy(() -> lpService.findDetailById(99L))
                    .isInstanceOf(LpNotFoundException.class);

            then(songRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Devuelve el DTO con el conteo de canciones cuando el LP existe")
        void devuelveDtoConSongCountCuandoLpExiste() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test Band", "description");
            Lp lp = new Lp("Test Lp", "description");
            lp.setArtist(artist);

            given(lpRepository.findByIdWithArtist(1L))
                    .willReturn(Optional.of(lp));
            given(songRepository.countByLpId(1L))
                    .willReturn(5L);

            // ---------- ACT ----------
            LpDetailResponse response = lpService.findDetailById(1L);

            // ---------- ASSERT ----------
            assertThat(response.name()).isEqualTo("Test Lp");
            assertThat(response.songCount()).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("create(LpCreateRequest request)")
    class Create {

        @Test
        @DisplayName("Lanza ArtistNotFoundException cuando el artistId no existe")
        void lanzaArtistNotFoundExceptionCuandoArtistIdNoExiste() {
            // ---------- ARRANGE ----------
            LpCreateRequest request = new LpCreateRequest("Test Lp", "description", 99L);
            given(artistRepository.findById(99L))
                    .willReturn(Optional.empty());

            // ---------- ACT + ASSERT ----------
            assertThatThrownBy(() -> lpService.create(request))
                    .isInstanceOf(ArtistNotFoundException.class);

            then(lpRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Lanza LpAlreadyExistsForArtistException cuando el name ya existe para el artist")
        void lanzaLpAlreadyExistsForArtistExceptionCuandoNameYaExisteParaArtist() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test Band", "description");
            Lp lpExistente = new Lp("Repetido", "otra description");
            LpCreateRequest request = new LpCreateRequest("Repetido", "description", 1L);

            given(artistRepository.findById(1L))
                    .willReturn(Optional.of(artist));
            given(lpRepository.findByArtistIdAndName(1L, "Repetido"))
                    .willReturn(Optional.of(lpExistente));

            // ---------- ACT + ASSERT ----------
            assertThatThrownBy(() -> lpService.create(request))
                    .isInstanceOf(LpAlreadyExistsForArtistException.class);

            then(lpRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("Persiste con el artist asignado cuando no hay colisión")
        void persisteConArtistAsignadoCuandoNoHayColision() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test Band", "description");
            LpCreateRequest request = new LpCreateRequest("Nuevo Lp", "description", 1L);

            given(artistRepository.findById(1L))
                    .willReturn(Optional.of(artist));
            given(lpRepository.findByArtistIdAndName(1L, "Nuevo Lp"))
                    .willReturn(Optional.empty());
            given(lpRepository.save(any(Lp.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // ---------- ACT ----------
            LpResponse response = lpService.create(request);

            // ---------- ASSERT ----------
            ArgumentCaptor<Lp> captor = ArgumentCaptor.forClass(Lp.class);
            then(lpRepository).should().save(captor.capture());

            assertThat(captor.getValue().getArtist()).isSameAs(artist);
            assertThat(response.name()).isEqualTo("Nuevo Lp");
        }
    }

    @Nested
    @DisplayName("update(Long id, LpUpdateRequest request)")
    class Update {

        @Test
        @DisplayName("Lanza LpNotFoundException cuando el id no existe")
        void lanzaLpNotFoundExceptionCuandoIdNoExiste() {
            // ---------- ARRANGE ----------
            LpUpdateRequest request = new LpUpdateRequest("Nuevo nombre", "description");
            given(lpRepository.findByIdWithArtist(99L))
                    .willReturn(Optional.empty());

            // ---------- ACT + ASSERT ----------
            assertThatThrownBy(() -> lpService.update(99L, request))
                    .isInstanceOf(LpNotFoundException.class);

            then(lpRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("No comprueba colisión cuando el nombre no cambia")
        void noCompruebaColisionCuandoNombreNoCambia() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test Band", "description");
            Lp lp = new Lp("Mismo nombre", "description antigua");
            lp.setArtist(artist);
            LpUpdateRequest request = new LpUpdateRequest("Mismo nombre", "description nueva");

            given(lpRepository.findByIdWithArtist(1L))
                    .willReturn(Optional.of(lp));
            given(lpRepository.save(any(Lp.class)))
                    .willReturn(lp);

            // ---------- ACT ----------
            LpResponse response = lpService.update(1L, request);

            // ---------- ASSERT ----------
            then(lpRepository).should(never()).findByArtistIdAndName(any(), any());
            assertThat(response.description()).isEqualTo("description nueva");
        }

        @Test
        @DisplayName("Lanza LpAlreadyExistsForArtistException cuando el nombre cambia y colisiona")
        void lanzaLpAlreadyExistsForArtistExceptionCuandoNombreCambiaYColisiona() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test Band", "description");
            Lp lp = new Lp("Original", "description");
            lp.setArtist(artist);
            Lp lpColision = new Lp("Nuevo nombre", "otra description");
            LpUpdateRequest request = new LpUpdateRequest("Nuevo nombre", "description");

            given(lpRepository.findByIdWithArtist(1L))
                    .willReturn(Optional.of(lp));
            given(lpRepository.findByArtistIdAndName(any(), eq("Nuevo nombre")))
                    .willReturn(Optional.of(lpColision));

            // ---------- ACT + ASSERT ----------
            assertThatThrownBy(() -> lpService.update(1L, request))
                    .isInstanceOf(LpAlreadyExistsForArtistException.class);

            then(lpRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("Persiste el cambio cuando no hay colisión")
        void persisteCambioCuandoNoHayColision() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test Band", "description");
            Lp lp = new Lp("Original", "description");
            lp.setArtist(artist);
            LpUpdateRequest request = new LpUpdateRequest("Nuevo nombre", "description nueva");

            given(lpRepository.findByIdWithArtist(1L))
                    .willReturn(Optional.of(lp));
            given(lpRepository.findByArtistIdAndName(any(), eq("Nuevo nombre")))
                    .willReturn(Optional.empty());
            given(lpRepository.save(any(Lp.class)))
                    .willReturn(lp);

            // ---------- ACT ----------
            LpResponse response = lpService.update(1L, request);

            // ---------- ASSERT ----------
            ArgumentCaptor<Lp> captor = ArgumentCaptor.forClass(Lp.class);
            then(lpRepository).should().save(captor.capture());
            assertThat(captor.getValue().getName()).isEqualTo("Nuevo nombre");
            assertThat(response.name()).isEqualTo("Nuevo nombre");
        }
    }

    @Nested
    @DisplayName("delete(Long id)")
    class Delete {

        @Test
        @DisplayName("Lanza LpNotFoundException cuando el id no existe")
        void lanzaLpNotFoundExceptionCuandoIdNoExiste() {
            // ---------- ARRANGE ----------
            given(lpRepository.findByIdWithArtist(99L))
                    .willReturn(Optional.empty());

            // ---------- ACT + ASSERT ----------
            assertThatThrownBy(() -> lpService.delete(99L))
                    .isInstanceOf(LpNotFoundException.class);

            then(lpRepository).should(never()).delete(any());
        }

        @Test
        @DisplayName("Persiste el borrado cuando el LP existe")
        void persisteBorradoCuandoLpExiste() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test Band", "description");
            Lp lp = new Lp("Test Lp", "description");
            lp.setArtist(artist);

            given(lpRepository.findByIdWithArtist(1L))
                    .willReturn(Optional.of(lp));

            // ---------- ACT ----------
            lpService.delete(1L);

            // ---------- ASSERT ----------
            then(lpRepository).should().delete(lp);
        }
    }
}
