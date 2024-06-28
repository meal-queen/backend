package com.threlease.base.functions.restaurant.dto;

import com.threlease.base.enums.OrderStatus;
import com.threlease.base.utils.enumeration.Enumeration;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

@Data
public class CheckPayDto {
    @UUID
    @NotEmpty
    @NotNull
    @NotBlank
    private String restaurant;

    @UUID
    @NotEmpty
    @NotNull
    @NotBlank
    private String order;

    @Enumeration(enumClass = OrderStatus.class)
    private OrderStatus status;
}
