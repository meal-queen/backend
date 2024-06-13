package com.threlease.base.functions.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FindByCodeDto {
    @NotEmpty
    @NotNull
    @NotBlank
    private String code;
}
