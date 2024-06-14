package com.threlease.base.functions.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class GetRestaurantListDto {
    @NotEmpty
    @NotBlank
    @NotNull
    private int page;

    @NotEmpty
    @NotBlank
    @NotNull
    private int take;
}
