package com.threlease.base.utils.moneyPin;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class Token {
    private String token;
    private long createdAt;
}
