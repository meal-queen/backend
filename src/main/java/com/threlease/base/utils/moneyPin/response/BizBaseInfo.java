package com.threlease.base.utils.moneyPin.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BizBaseInfo {
    @JsonProperty("bizNo")
    private String bizNo;

    @JsonProperty("bizName")
    private String bizName;

    @JsonProperty("ceoName")
    private String ceoName;

    @JsonProperty("zipCode")
    private String zipCode;

    @JsonProperty("address")
    private String address;

    @JsonProperty("bizType")
    private String bizType;

    @JsonProperty("bizStatus")
    private String bizStatus;

    @JsonProperty("taxType")
    private String taxType;

    @JsonProperty("simplifiedTaxTypeDate")
    private String simplifiedTaxTypeDate;

    @JsonProperty("closingDate")
    private String closingDate;
}
