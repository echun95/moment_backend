package com.moment.common.exception.member;

import com.moment.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    FAILED_SEND_AUTHENTICATION_EMAIL(10001, "인증 메일 발송을 실패했습니다. 다시 시도해주세요."),
    FAILED_VERIFY_EMAIL(10002, "잘못된 인증 코드입니다. 다시 시도해주세요."),
    DUPLICATE_MEMBER(10003, "이미 회원가입이 되어있는 계정입니다."),
    ;

    private final int code;
    private final String message;
}
