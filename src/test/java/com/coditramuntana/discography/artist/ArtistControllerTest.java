package com.coditramuntana.discography.artist;

import com.coditramuntana.discography.artist.dto.ArtistCreateRequest;
import com.coditramuntana.discography.artist.dto.ArtistDetailResponse;
import com.coditramuntana.discography.artist.dto.ArtistResponse;
import com.coditramuntana.discography.artist.dto.ArtistUpdateRequest;
import com.coditramuntana.discography.artist.exception.ArtistHasLpsException;
import com.coditramuntana.discography.artist.exception.ArtistNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArtistController.class)
class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ArtistService artistService;

    @Nested
    @DisplayName("GET /api/artists/{id}")
    class FindDetailById {

        @Test
        @DisplayName("Devuelve 200 y detalle del artista cuando existe")
        void devuelve200YDetailDelArtistCuandoExiste() throws Exception {
            // ---------- ARRANGE ----------
            ArtistDetailResponse response = new ArtistDetailResponse(
                    1L, "Metallica", "Thrash metal band from Los Angeles", 5L
            );
            given(artistService.findDetailById(1L))
                    .willReturn(response);

            // ---------- ACT + ASSERT ----------
            mockMvc.perform(get("/api/artists/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Metallica"))
                    .andExpect(jsonPath("$.lpCount").value(5));
        }

        @Test
        @DisplayName("Devuelve 404 y ProblemDetail cuando el artista no existe")
        void devuelve404YProblemDetailCuandoArtistNoExiste() throws Exception {
            // ARRANGE
            given(artistService.findDetailById(99L))
                    .willThrow(new ArtistNotFoundException(99L));

            // ACT + ASSERT
            mockMvc.perform(get("/api/artists/99")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Resource not found"))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.resourceType").value("Artist"))
                    .andExpect(jsonPath("$.resourceId").value(99));
        }

    }

    @Nested
    @DisplayName("POST /api/artists")
    class Create {

        @Test
        @DisplayName("Devuelve 400 y ProblemDetail con fieldErrors cuando el name está vacío")
        void devuelve400YProblemDetailConFieldErrorsCuandoNameEstaVacio() throws Exception {
            // ARRANGE
            ArtistCreateRequest request = new ArtistCreateRequest("", "description");


            // ACT + ASSERT
            mockMvc.perform(post("/api/artists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors.name").exists());
        }
    }

    @Nested
    @DisplayName("PUT /api/artists/{id}")
    class Update {

        @Test
        @DisplayName("Devuelve 200 y ArtistResponse cuando el body es válido")
        void devuelve200YArtistResponseCuandoBodyValido() throws Exception {
            // ARRANGE
            ArtistUpdateRequest request = new ArtistUpdateRequest("Metallica", "Thrash metal band from Los Angeles");
            ArtistResponse response = new ArtistResponse(1L, "Metallica", "Thrash metal band from Los Angeles");
            given(artistService.update(1L, request))
                    .willReturn(response);

            // ACT + ASSERT
            mockMvc.perform(put("/api/artists/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Metallica"))
                    .andExpect(jsonPath("$.description").value("Thrash metal band from Los Angeles"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/artists/{id}")
    class Delete {

        @Test
        @DisplayName("Devuelve 409 y ProblemDetail cuando tiene LPs")
        void devuelve409YProblemDetailCuandoTieneLps() throws Exception {
            // ARRANGE
            willThrow(new ArtistHasLpsException(1L, 3L))
                    .given(artistService).delete(1L);

            // ACT + ASSERT
            mockMvc.perform(delete("/api/artists/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("Resource conflict"));
        }
    }
}

