package com.threlease.base.functions.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Data
public class UpdateCompanyDto {
    @NotEmpty
    @NotBlank
    @NotNull
    private String company;

    private Optional<String> bizNumber;

    private Optional<String> team;

    private Optional<MultipartFile> file;
}
