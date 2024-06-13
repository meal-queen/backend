package com.threlease.base.functions.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Data
public class CreateCompanyDto {
    @NotEmpty
    @NotBlank
    @NotNull
    private String bizNumber;

    @NotEmpty
    @NotBlank
    @NotNull
    private String team;

    private Optional<MultipartFile> file;
}
