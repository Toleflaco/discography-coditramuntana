package com.coditramuntana.discography.lp;

import com.coditramuntana.discography.artist.dto.ArtistResponse;
import com.coditramuntana.discography.artist.exception.ArtistNotFoundException;
import com.coditramuntana.discography.lp.dto.LpCreateRequest;
import com.coditramuntana.discography.lp.dto.LpDetailResponse;
import com.coditramuntana.discography.lp.dto.LpResponse;
import com.coditramuntana.discography.lp.dto.LpUpdateRequest;
import com.coditramuntana.discography.lp.exception.LpAlreadyExistsForArtistException;
import com.coditramuntana.discography.lp.exception.LpNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LpController.class)
class LpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private LpService lpService;

    @Nested
    @DisplayName("GET /api/lps/{id}")
    class FindById {

        @Test
        @DisplayName("Devuelve 200 y detalle del LP cuando existe")
        void devuelve200YDetailDelLpCuandoExiste() throws Exception {
            // ---------- ARRANGE ----------
            ArtistResponse artist = new ArtistResponse(1L, "Metallica", "Thrash metal band from Los Angeles");
            LpDetailResponse response = new LpDetailResponse(
                    1L, "Master of Puppets", "Third studio album", artist, 8L
            );
            given(lpService.findDetailById(1L))
                    .willReturn(response);

            // ---------- ACT + ASSERT ----------
            mockMvc.perform(get("/api/lps/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Master of Puppets"))
                    .andExpect(jsonPath("$.artist.name").value("Metallica"))
                    .andExpect(jsonPath("$.songCount").value(8));
        }

        @Test
        @DisplayName("Devuelve 404 y ProblemDetail cuando el LP no existe")
        void devuelve404YProblemDetailCuandoLpNoExiste() throws Exception {
            // ARRANGE
            given(lpService.findDetailById(99L))
                    .willThrow(new LpNotFoundException(99L));

            // ACT + ASSERT
            mockMvc.perform(get("/api/lps/99")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Resource not found"))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.resourceType").value("Lp"))
                    .andExpect(jsonPath("$.resourceId").value(99));
        }

    }

    @Nested
    @DisplayName("POST /api/lps")
    class Create {

        @Test
        @DisplayName("Devuelve 400 y ProblemDetail con fieldErrors cuando el name está vacío")
        void devuelve400YProblemDetailConFieldErrorsCuandoNameEstaVacio() throws Exception {
            // ARRANGE
            LpCreateRequest request = new LpCreateRequest("", "description", 1L);

            // ACT + ASSERT
            mockMvc.perform(post("/api/lps")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors.name").exists());
        }

        @Test
        @DisplayName("Devuelve 404 y ProblemDetail cuando el artistId no existe")
        void devuelve404YProblemDetailCuandoArtistIdNoExiste() throws Exception {
            // ARRANGE
            LpCreateRequest request = new LpCreateRequest("Master of Puppets", "Third studio album", 99L);
            given(lpService.create(request))
                    .willThrow(new ArtistNotFoundException(99L));

            // ACT + ASSERT
            mockMvc.perform(post("/api/lps")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Resource not found"))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.resourceType").value("Artist"))
                    .andExpect(jsonPath("$.resourceId").value(99));
        }

        @Test
        @DisplayName("Devuelve 201 y header Location cuando el body es válido")
        void devuelve201YLocationHeaderCuandoBodyValido() throws Exception {
            // ARRANGE
            LpCreateRequest request = new LpCreateRequest("Master of Puppets", "Third studio album", 1L);
            LpResponse response = new LpResponse(1L, "Master of Puppets", "Third studio album", "Metallica");
            given(lpService.create(request))
                    .willReturn(response);

            // ACT + ASSERT
            mockMvc.perform(post("/api/lps")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString("/api/lps/")))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Master of Puppets"));
        }
    }

    @Nested
    @DisplayName("PUT /api/lps/{id}")
    class Update {

        @Test
        @DisplayName("Devuelve 409 y ProblemDetail cuando el nombre colisiona")
        void devuelve409YProblemDetailCuandoNombreColisiona() throws Exception {
            // ARRANGE
            LpUpdateRequest request = new LpUpdateRequest("Ride the Lightning", "Second studio album");
            given(lpService.update(1L, request))
                    .willThrow(new LpAlreadyExistsForArtistException(1L, "Ride the Lightning"));

            // ACT + ASSERT
            mockMvc.perform(put("/api/lps/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("Resource conflict"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/lps/{id}")
    class Delete {

        @Test
        @DisplayName("Devuelve 204 cuando el LP existe")
        void devuelve204CuandoLpExiste() throws Exception {
            // ARRANGE
            willDoNothing().given(lpService).delete(1L);

            // ACT + ASSERT
            mockMvc.perform(delete("/api/lps/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }
}
