package com.coditramuntana.discography.artist;

import com.coditramuntana.discography.artist.dto.ArtistCreateRequest;
import com.coditramuntana.discography.artist.dto.ArtistDetailResponse;
import com.coditramuntana.discography.artist.dto.ArtistResponse;
import com.coditramuntana.discography.artist.dto.ArtistUpdateRequest;
import com.coditramuntana.discography.lp.dto.LpResponse;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @Operation(
            summary = "Lista paginada de Artistas",
            description = "Devuelve Artists con paginación. Por defecto ordenados por " +
                    "nombre del artista "
    )
    @GetMapping
    public Page<ArtistResponse> findAll(Pageable pageable) {
        return artistService.findAll(pageable);

    }


    @Operation(
            summary = "Muestra el detalle de un Artist",
            description = "Devuelve el detalle de un Artist buscado por su id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Artist encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ArtistDetailResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El Artist referenciado por id no existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ArtistDetailResponse findById(@PathVariable Long id) {
        return artistService.findDetailById(id);
    }


    @Operation(
            summary = "Crear un nuevo Artist",
            description = "Crea un Artist"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Artist creado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ArtistResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Body inválido (fallos de validación en name, description o artistId)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe un Artist con ese name",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ArtistResponse> create(@Valid @RequestBody ArtistCreateRequest request) {
        ArtistResponse created = artistService.create(request);


        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Operation(
            summary = "Actualizar un Artist",
            description = "Actualiza un Artist existente. "
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Artist actualizado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ArtistResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Body inválido (fallos de validación en name, description o artistId)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El Artist referenciado por id no existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe un Artist con ese nombre",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ArtistResponse update(@PathVariable Long id, @Valid @RequestBody ArtistUpdateRequest request) {
        return artistService.update(id, request);
    }


    @Operation(
            summary = "Borra un Artist",
            description = "Borra un Artist asociado a un Id "

    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Artist borrado correctamente"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El Artist tiene Lps asociados no se puede borrar",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El Artist referenciado por Id no existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        artistService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
