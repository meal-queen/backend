package com.threlease.base.functions.company.dto;

import com.threlease.base.enums.AffiliationUserRoles;
import com.threlease.base.utils.enumeration.Enumeration;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

@Data
public class GetUsersDto {
    @NotEmpty
    @NotBlank
    @NotNull
    @UUID
    private String company;

    @Enumeration(enumClass = AffiliationUserRoles.class, optional = true)
    private AffiliationUserRoles role;
}
