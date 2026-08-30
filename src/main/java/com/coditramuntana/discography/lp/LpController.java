package com.coditramuntana.discography.lp;

import com.coditramuntana.discography.lp.dto.LpCreateRequest;
import com.coditramuntana.discography.lp.dto.LpDetailResponse;
import com.coditramuntana.discography.lp.dto.LpResponse;
import com.coditramuntana.discography.lp.dto.LpUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping
    public Page<LpResponse> findAll(
            @RequestParam(required = false) String artistName,
            Pageable pageable
    ) {
        return lpService.findAll(artistName, pageable);
    }

    @GetMapping("/{id}")
    public LpDetailResponse findById(@PathVariable Long id) {
        return lpService.findDetailById(id);
    }

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

    @PutMapping("/{id}")
    public LpResponse update(@PathVariable Long id, @Valid @RequestBody LpUpdateRequest request) {
        return lpService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lpService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
