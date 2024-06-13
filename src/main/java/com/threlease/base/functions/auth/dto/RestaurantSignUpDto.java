package com.threlease.base.functions.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Data
public class RestaurantSignUpDto {
    @NotEmpty
    @NotBlank
    @NotNull
    private String username;

    @NotEmpty
    @NotBlank
    @NotNull
    private String password;

    @NotEmpty
    @NotBlank
    @NotNull
    private String name;

    @NotEmpty
    @NotBlank
    @NotNull
    private String bizNumber;

    private Optional<MultipartFile> file;
}
