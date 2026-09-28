package com.coditramuntana.discography.artist;

import com.coditramuntana.discography.artist.dto.ArtistCreateRequest;
import com.coditramuntana.discography.artist.dto.ArtistDetailResponse;
import com.coditramuntana.discography.artist.dto.ArtistResponse;
import com.coditramuntana.discography.artist.dto.ArtistUpdateRequest;
import com.coditramuntana.discography.artist.exception.ArtistHasLpsException;
import com.coditramuntana.discography.artist.exception.ArtistNameAlreadyExistsException;
import com.coditramuntana.discography.artist.exception.ArtistNotFoundException;
import com.coditramuntana.discography.lp.LpRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceTest {

    @Mock
    private LpRepository lpRepository;

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistService artistService;


    @Nested
    @DisplayName("delete(Long id)")
    class Delete {
        @Test
        @DisplayName("Comprueba que lanza ArtistNotFoundException cuando el artist no existe")
        void compruebaArtistNotFound() {
            // ---------- ARRANGE ----------
            given(artistRepository.findById(99L))
                    .willReturn(Optional.empty());

            // ---------- ACT + ASSERT ----------
            assertThatThrownBy(() -> artistService.delete(99L))
                    .isInstanceOf(ArtistNotFoundException.class);

            // El artist no existe: nunca debe llegar al lpRepository
            then(lpRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Comprueba que lanza ArtistHasLpsException cuando el artist tiene LPs")
        void compruebaArtistHasLpsException() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test band", "description");

            given(artistRepository.findById(1L))
                    .willReturn(Optional.of(artist));
            given(lpRepository.countByArtistId(1L))
                    .willReturn(1L);

            // ---------- ACT + ASSERT ----------
            assertThatThrownBy(() -> artistService.delete(1L))
                    .isInstanceOf(ArtistHasLpsException.class);

        }

        @Test
        @DisplayName("Persiste el borrado cuando no tiene LPs")
        void persisteBorradoCuandoNoTieneLps() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test band", "description");
            given(artistRepository.findById(1L))
                    .willReturn(Optional.of(artist));
            given(lpRepository.countByArtistId(1L))
                    .willReturn(0L);

            // ---------- ACT ------------
            artistService.delete(1L);

            // ---------- ASSERT ----------
            then(artistRepository).should().delete(artist);
        }
    }

    @Nested
    @DisplayName("update(Long id, ArtistUpdateRequest request)")
    class Update {

        @Test
        @DisplayName("Lanza ArtistNotFoundException cuando el id no existe")
        void lanzaArtistNotFoundExceptionCuandoIdNoExiste() {
            // ---------- ARRANGE ----------
            ArtistUpdateRequest request = new ArtistUpdateRequest("Test Band", "description");
            given(artistRepository.findById(99L))
                    .willReturn(Optional.empty());

            // ---------- ACT + ASSERT ------------
            assertThatThrownBy(() -> artistService.update(99L, request))
                    .isInstanceOf(ArtistNotFoundException.class);
            then(lpRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Lanza ArtistNameAlreadyExistsException cuando el nuevo name colisiona")
        void lanzaArtistNameAlreadyExistsExceptionCuandoNuevoNameColisiona() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Original", "description");
            Artist otherArtist = new Artist("YaEnBD", "description");
            ArtistUpdateRequest request = new ArtistUpdateRequest("Solicitado", "desc");

            given(artistRepository.findById(1L))
                    .willReturn(Optional.of(artist));
            given(artistRepository.findByName("Solicitado"))
                    .willReturn(Optional.of(otherArtist));
            // ---------- ACT + ASSERT ------------
            assertThatThrownBy(() -> artistService.update(1L, request))
                    .isInstanceOf(ArtistNameAlreadyExistsException.class);
            then(artistRepository).should(never()).save(any());


        }

        @Test
        @DisplayName("Actualiza y devuelve el DTO cuando no hay colisión")
        void actualizaYDevuelveDtoCuandoNoHayColision() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Original", "description");
            ArtistUpdateRequest request = new ArtistUpdateRequest("Nuevo", "description");

            given(artistRepository.findById(1L))
                    .willReturn(Optional.of(artist));
            given(artistRepository.findByName("Nuevo"))
                    .willReturn(Optional.empty());
            given(artistRepository.save(any(Artist.class)))
                    .willReturn(artist);

            // ---------- ACT ------------------
            ArtistResponse response = artistService.update(1L, request);


            // ---------  ASSERT ------------
            then(artistRepository).should().save(artist);
            assertThat(response.name()).isEqualTo("Nuevo");
        }

    }

    @Nested
    @DisplayName("create(ArtistCreateRequest request)")
    class Create {

        @Test
        @DisplayName("Lanza ArtistNameAlreadyExistsException cuando el name ya existe")
        void lanzaArtistNameAlreadyExistsExceptionCuandoNameYaExiste() {

            // ---------- ARRANGE ----------
            Artist artist = new Artist("Ya estoy", "description");
            ArtistCreateRequest request = new ArtistCreateRequest("Ya estoy", "description");
            given(artistRepository.findByName("Ya estoy"))
                    .willReturn(Optional.of(artist));

            // ---------- ACT + ASSERT ------------
            assertThatThrownBy(() -> artistService.create(request))
                    .isInstanceOf(ArtistNameAlreadyExistsException.class);
            then(artistRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("Persiste y devuelve el DTO cuando no hay colisión")
        void persisteYDevuelveDtoCuandoNoHayColision() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Nuevo", "description");
            ArtistCreateRequest request = new ArtistCreateRequest("Nuevo", "description");
            given(artistRepository.findByName("Nuevo"))
                    .willReturn(Optional.empty());
            given(artistRepository.save(any(Artist.class)))
                    .willReturn(artist);

            // ---------- ACT ------------------
            ArtistResponse response = artistService.create(request);


            // ---------  ASSERT ------------
            then(artistRepository).should().save(any(Artist.class));
            assertThat(response.name()).isEqualTo("Nuevo");

        }
    }

    @Nested
    @DisplayName("findDetailById(Long id)")
    class FindDetailById {

        @Test
        @DisplayName("Lanza ArtistNotFoundException cuando el id no existe")
        void lanzaArtistNotFoundExceptionCuandoIdNoExiste() {
            // ---------- ARRANGE ----------
            given(artistRepository.findById(99L))
                    .willReturn(Optional.empty());

            // ---------- ACT + ASSERT ------------------
            assertThatThrownBy(() -> artistService.findDetailById(99L))
                    .isInstanceOf(ArtistNotFoundException.class);
            then(lpRepository).shouldHaveNoInteractions();
        }


        @Test
        @DisplayName("Devuelve el detalle con el conteo de LPs cuando el artist existe")
        void devuelveDetailConLpCountCuandoArtistExiste() {
            // ---------- ARRANGE ----------
            Artist artist = new Artist("Test Band", "description");
            given(artistRepository.findById(1L))
                    .willReturn(Optional.of(artist));
            given(lpRepository.countByArtistId(1L))
                    .willReturn(5L);

            // --------- ACT ----------
            ArtistDetailResponse response = artistService.findDetailById(1L);


            // ---------- ASSERT ----------
            assertThat(response.name()).isEqualTo(artist.getName());
            assertThat(response.lpCount()).isEqualTo(5L);

        }
    }

    @Nested
    @DisplayName("findAll(String name, String description, Pageable pageable)")
    class FindAll {
        @Test
        @DisplayName("Aplica el sort por defecto y no filtra cuando ambos filtros son nulos")
        void aplicaSortPorDefectoYNoFiltraCuandoAmbosFiltrosSonNulos() {
            // ---------- ARRANGE ----------

            given(artistRepository.findAll(any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of()));

            // Pageable SIN sort (el cliente no mandó ?sort=)
            Pageable pageableSinSort = PageRequest.of(0, 10);

            // --------- ACT ----------

            artistService.findAll(null, null, pageableSinSort);
            // ---------- ASSERT ----------

            // Capturamos el Pageable que el service pasó al repository
            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            then(artistRepository).should().findAll(captor.capture());
            then(artistRepository).should(never()).findByNameContainingIgnoreCase(any(), any());
            then(artistRepository).should(never()).findByDescriptionContainingIgnoreCase(any(), any());

            // Sacamos el Pageable capturado y verificamos su sort
            Sort sortAplicado = captor.getValue().getSort();

            assertThat(sortAplicado).isEqualTo(Sort.by("name"));
        }

        @Test
        @DisplayName("No filtra cuando ambos filtros solo contienen espacios")
        void noFiltraCuandoAmbosFiltrosSoloContienenEspacios() {
            given(artistRepository.findAll(any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of()));

            artistService.findAll("   ", "  ", PageRequest.of(0, 10));

            then(artistRepository).should().findAll(any(Pageable.class));
            then(artistRepository).should(never()).findByNameContainingIgnoreCase(any(), any());
            then(artistRepository).should(never()).findByDescriptionContainingIgnoreCase(any(), any());
        }

        @Test
        @DisplayName("Filtra por nombre parcial ignorando mayúsculas y minúsculas")
        void filtraPorNombreParcialIgnorandoMayusculasYMinusculas() {
            Artist artist = new Artist("Metallica", "description");
            given(artistRepository.findByNameContainingIgnoreCase(
                    "tAl", PageRequest.of(0, 10, Sort.by("name"))
            )).willReturn(new PageImpl<>(List.of(artist)));

            Page<ArtistResponse> result = artistService.findAll("tAl", null, PageRequest.of(0, 10));

            then(artistRepository).should().findByNameContainingIgnoreCase(
                    "tAl", PageRequest.of(0, 10, Sort.by("name"))
            );
            then(artistRepository).should(never()).findAll(any(Pageable.class));
            assertThat(result.getContent()).extracting(ArtistResponse::name).containsExactly("Metallica");
        }

        @Test
        @DisplayName("Elimina espacios exteriores del filtro y conserva los interiores")
        void eliminaEspaciosExterioresYConservaEspaciosInteriores() {
            String normalizedName = "Mixed Case";
            String normalizedDescription = "Heavy Metal";
            given(artistRepository.findByNameContainingIgnoreCase(
                    normalizedName, PageRequest.of(0, 10, Sort.by("name"))
            )).willReturn(new PageImpl<>(List.of()));
            given(artistRepository.findByDescriptionContainingIgnoreCase(
                    normalizedDescription, PageRequest.of(0, 10, Sort.by("name"))
            )).willReturn(new PageImpl<>(List.of()));

            artistService.findAll("  Mixed Case  ", null, PageRequest.of(0, 10));
            artistService.findAll(null, "  Heavy Metal  ", PageRequest.of(0, 10));

            then(artistRepository).should().findByNameContainingIgnoreCase(
                    normalizedName, PageRequest.of(0, 10, Sort.by("name"))
            );
            then(artistRepository).should().findByDescriptionContainingIgnoreCase(
                    normalizedDescription, PageRequest.of(0, 10, Sort.by("name"))
            );
        }

        @Test
        @DisplayName("Filtra por descripción parcial ignorando mayúsculas y minúsculas")
        void filtraPorDescripcionParcial() {
            Artist artist = new Artist("Metallica", "Thrash metal band");
            given(artistRepository.findByDescriptionContainingIgnoreCase(
                    "mEtAl", PageRequest.of(0, 10, Sort.by("name"))
            )).willReturn(new PageImpl<>(List.of(artist)));

            Page<ArtistResponse> result = artistService.findAll(null, "mEtAl", PageRequest.of(0, 10));

            then(artistRepository).should().findByDescriptionContainingIgnoreCase(
                    "mEtAl", PageRequest.of(0, 10, Sort.by("name"))
            );
            then(artistRepository).should(never()).findAll(any(Pageable.class));
            assertThat(result.getContent()).extracting(ArtistResponse::name).containsExactly("Metallica");
        }

        @Test
        @DisplayName("Combina name y description con AND y normaliza ambos filtros")
        void combinaAmbosFiltrosConAnd() {
            String normalizedName = "Metal";
            String normalizedDescription = "Thrash";
            given(artistRepository.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(
                    normalizedName, normalizedDescription, PageRequest.of(0, 10, Sort.by("name"))
            )).willReturn(new PageImpl<>(List.of()));

            artistService.findAll("  Metal  ", "  Thrash  ", PageRequest.of(0, 10));

            then(artistRepository).should().findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(
                    normalizedName, normalizedDescription, PageRequest.of(0, 10, Sort.by("name"))
            );
            then(artistRepository).should(never()).findByNameContainingIgnoreCase(any(), any());
            then(artistRepository).should(never()).findByDescriptionContainingIgnoreCase(any(), any());
        }
    }
}
