package com.threlease.base.functions.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

@Data
public class DeleteCompanyDto {
    @NotEmpty
    @NotBlank
    @NotNull
    @UUID
    private String company;
}
