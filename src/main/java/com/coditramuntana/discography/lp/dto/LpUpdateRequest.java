package com.coditramuntana.discography.lp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LpUpdateRequest(


        @NotBlank
        @Size(min = 1, max = 100)
        String name,

        @NotBlank
        @Size(max = 5000)
        String description

) {
}
