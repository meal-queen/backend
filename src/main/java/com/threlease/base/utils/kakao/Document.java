package com.threlease.base.utils.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {
    @JsonProperty("road_address")
    private Address road_address;

    @JsonProperty("x")
    private String x;

    @JsonProperty("y")
    private String y;
}
