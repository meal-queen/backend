package com.threlease.base.functions.company.dto;

import com.threlease.base.enums.AffiliationUserRoles;
import com.threlease.base.utils.enumeration.Enumeration;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

@Data
public class UpdatePermissionDto {
    @NotEmpty
    @NotBlank
    @NotNull
    @UUID
    private String company;

    @NotEmpty
    @NotBlank
    @NotNull
    @UUID
    private String author;

    @Enumeration(enumClass = AffiliationUserRoles.class)
    AffiliationUserRoles role;
}
