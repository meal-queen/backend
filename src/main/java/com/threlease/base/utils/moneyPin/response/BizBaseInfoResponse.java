package com.threlease.base.utils.moneyPin.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BizBaseInfoResponse {
    @JsonProperty("reqBizNo")
    private String reqBizNo;

    @JsonProperty("info")
    private BizBaseInfo info;

    @JsonProperty("error")
    private String error;
}
