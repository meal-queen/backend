package com.threlease.base.functions.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

@Data
public class GetRestaurantPayLogDto {
    @NotEmpty
    @NotNull
    @NotBlank
    private String restaurant;
}
