package com.threlease.base.utils.kakao;

import lombok.Data;

import java.util.List;

@Data
public class KakaoResponse {
    private List<Document> documents;
    private Object meta;
}
