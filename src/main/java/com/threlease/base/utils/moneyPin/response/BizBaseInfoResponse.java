package com.threlease.base.utils.moneyPin.response;

import lombok.Data;

@Data
public class BizBaseInfoResponse {
    private String reqBizNo;
    private BizBaseInfo info;
    private String error;
}
