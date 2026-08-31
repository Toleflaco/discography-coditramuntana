package com.coditramuntana.discography.lp;

import com.coditramuntana.discography.lp.dto.LpCreateRequest;
import com.coditramuntana.discography.lp.dto.LpDetailResponse;
import com.coditramuntana.discography.lp.dto.LpResponse;
import com.coditramuntana.discography.lp.dto.LpUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/lps")
public class LpController {

    private final LpService lpService;


    public LpController(LpService lpService) {
        this.lpService = lpService;
    }

    @Operation(
            summary = "Lista paginada de LPs",
            description = "Devuelve LPs con paginación. Por defecto ordenados por " +
                    "nombre del artista y luego por nombre del LP. Filtro opcional " +
                    "por nombre parcial del artista (case-insensitive)."
    )
    @GetMapping
    public Page<LpResponse> findAll(
            @RequestParam(required = false) String artistName,
            Pageable pageable
    ) {
        return lpService.findAll(artistName, pageable);
    }


    @Operation(
            summary = "Muestra el detalle de un Lp",
            description = "Devuelve el detalle de un Lp buscado por su id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "LP encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LpDetailResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El LP referenciado por id no existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public LpDetailResponse findById(@PathVariable Long id) {
        return lpService.findDetailById(id);
    }


    @Operation(
            summary = "Crear un nuevo LP",
            description = "Crea un LP asociado a un Artist existente. " +
                    "El par (artistId, name) debe ser único."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "LP creado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LpResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Body inválido (fallos de validación en name, description)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El Artist referenciado por artistId no existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe un LP con ese name para el mismo artistId",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<LpResponse> create(@Valid @RequestBody LpCreateRequest request) {
        LpResponse created = lpService.create(request);


        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }


    @Operation(
            summary = "Actualiza un LP",
            description = "Actualiza un LP asociado a un Artist existente. "

    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "LP actualizado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LpResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Body inválido (fallos de validación en name, description)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El Lp referenciado por Id no existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe un LP con ese name para el mismo artistId",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public LpResponse update(@PathVariable Long id, @Valid @RequestBody LpUpdateRequest request) {
        return lpService.update(id, request);
    }


    @Operation(
            summary = "Borra un LP",
            description = "Borra un Lp asociado a un Id "

    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "LP borrado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El Lp referenciado por Id no existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lpService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
