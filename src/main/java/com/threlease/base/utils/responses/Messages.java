package com.threlease.base.utils.responses;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Messages {
    public final static String ERROR_SESSION = "세션이 만료 되었거나 인증에 문제가 생겼습니다.";

    public final static String NOT_PERMISSION = "해당 기능에 접근할 권한이 없습니다.";

    public final static String NOT_FOUND_USER = "해당 유저를 찾을 수 없습니다.";

    public final static String NOT_FOUND_COMPANY = "해당 소속을 찾을 수 없습니다.";

    public final static String NOT_FOUND_RESTAURANT = "해당 가맹점을 찾을 수 없습니다.";

    public final static String BAD_REQUEST_LOGIN = "아이디 혹은 비밀번호를 확인해주세요.";

    public final static String BAD_REQUEST = "정상적인 접근이 아닙니다.";

    public final static String DUPLICATION_USER = "해당 유저(이)가 이미 존재 합니다.";

    public final static String DUPLICATION_COMPANY = "해당 소속(이)가 이미 존재 합니다.";

    public final static String DUPLICATION_COMPANY_CASE_NEW = "이미 해당 소속(이)가 존재 합니다. 소속 이름과 팀을 다시 확인해주세요.";

    public final static String DUPLICATION_RESTAURANT = "해당 가맹점(이)가 이미 존재 합니다.";

    public final static String DUPLICATION_RESTAURANT_CASE_NEW = "이미 해당 가맹점(이)가 존재 합니다. 가맹점 이름을 다시 확인해주세요.";
}
