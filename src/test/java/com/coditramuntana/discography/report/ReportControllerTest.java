package com.coditramuntana.discography.report;

import com.coditramuntana.discography.report.dto.DiscographyReportRow;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiscographyReportService discographyReportService;

    @Nested
    @DisplayName("GET /api/reports/discography")
    class GenerateReport {

        @Test
        @DisplayName("Devuelve 200 y fila del reporte cuando hay datos")
        void devuelve200YFilaDelReporteCuandoHayDatos() throws Exception {
            // ---------- ARRANGE ----------
            DiscographyReportRow row = new DiscographyReportRow(
                    "Master of Puppets", "Metallica", 8L, "Cliff Burton, James Hetfield, Kirk Hammett"
            );
            Page<DiscographyReportRow> page = new PageImpl<>(List.of(row));
            given(discographyReportService.generateReport(any()))
                    .willReturn(page);

            // ---------- ACT + ASSERT ----------
            mockMvc.perform(get("/api/reports/discography")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].lpName").value("Master of Puppets"))
                    .andExpect(jsonPath("$.content[0].artistName").value("Metallica"))
                    .andExpect(jsonPath("$.content[0].songCount").value(8))
                    .andExpect(jsonPath("$.content[0].authorsCsv").value("Cliff Burton, James Hetfield, Kirk Hammett"));
        }

        @Test
        @DisplayName("Devuelve 200 y content vacío cuando no hay LPs")
        void devuelve200YContentVacioCuandoNoHayLps() throws Exception {
            // ARRANGE
            Page<DiscographyReportRow> emptyPage = new PageImpl<>(List.of());
            given(discographyReportService.generateReport(any()))
                    .willReturn(emptyPage);

            // ACT + ASSERT
            mockMvc.perform(get("/api/reports/discography")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        @DisplayName("Pasa los parámetros de paginación custom al service")
        void pasaLosParametrosDePaginacionCustomAlService() throws Exception {
            // ARRANGE
            Page<DiscographyReportRow> emptyPage = new PageImpl<>(List.of());
            given(discographyReportService.generateReport(any()))
                    .willReturn(emptyPage);

            // ACT
            mockMvc.perform(get("/api/reports/discography")
                            .param("page", "2")
                            .param("size", "5")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // ASSERT
            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            then(discographyReportService).should().generateReport(pageableCaptor.capture());
            assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(2);
            assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(5);
        }
    }
}
