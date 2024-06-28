package com.threlease.base.functions.pay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

@Data
public class PayDto {
    @NotEmpty
    @NotBlank
    @NotNull
    private String company;

    @NotEmpty
    @NotBlank
    @NotNull
    private String restaurant;

    @NotEmpty
    @NotBlank
    @NotNull
    private int point;
}
