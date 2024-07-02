package com.threlease.base.utils.moneyPin;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InfoBaseRequest {
    private List<String> bizNoList;
}
