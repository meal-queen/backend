package com.threlease.base.functions.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Optional;

@Getter
public class GetRestaurnatList {
    Optional<String> address;

    Optional<String> search;

    @NotEmpty
    @NotNull
    @NotBlank
    private int page;

    @NotEmpty
    @NotNull
    @NotBlank
    private int take;
}
