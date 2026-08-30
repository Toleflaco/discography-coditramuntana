package com.coditramuntana.discography.lp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record LpCreateRequest(

        @NotBlank
        @Size(min = 1, max = 100)
        String name,

        @NotBlank
        @Size(max = 5000)
        String description,

        @NotNull
        @Positive
        Long artistId
) {
}
