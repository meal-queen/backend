package com.threlease.base.utils.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoResponse {
    @JsonProperty("documents")
    private List<Document> documents;

    @JsonProperty("meta")
    private Object meta;
}
