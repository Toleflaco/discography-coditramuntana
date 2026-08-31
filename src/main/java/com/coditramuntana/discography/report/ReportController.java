package com.coditramuntana.discography.report;

import com.coditramuntana.discography.report.dto.DiscographyReportRow;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final DiscographyReportService discographyReportService;


    public ReportController(DiscographyReportService discographyReportService) {
        this.discographyReportService = discographyReportService;
    }

    @Operation(
            summary = "Reporte de discografía",
            description = "Devuelve fila por LP con nombre del LP, nombre del artista, " +
                    "número de canciones y autores concatenados (deduplicados, alfabéticos). " +
                    "Paginado."
    )
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "Reporte generado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DiscographyReportRow.class)
                    )
            )
    )
    @GetMapping("/discography")
    public Page<DiscographyReportRow> generateReport(Pageable pageable){
        return discographyReportService.generateReport(pageable);
    }
}
