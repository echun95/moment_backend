package com.moment.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

	    INVALID_PARAMETER(HttpStatus.BAD_REQUEST.value(), "잘못된 매개변수가 포함됐습니다."),
	    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "리소스를 찾을 수 없습니다."),
	    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류가 발생했습니다. \\n새로고침 후 다시 시도해주시고 에러가 지속적으로 발생할 경우 관리자에게 문의해주시길 바랍니다."),
		EXPIRED_JWT(HttpStatus.UNAUTHORIZED.value(), "로그인 시간이 만료됐습니다. 다시 로그인 해주세요")
	;
	private final int code;
	private final String message;

}

