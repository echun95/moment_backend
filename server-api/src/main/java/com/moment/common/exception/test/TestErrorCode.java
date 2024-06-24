package com.moment.common.exception.test;

import com.moment.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TestErrorCode implements ErrorCode {

	    TEST_ERROR_1(10001, "에러코드 테스트입니다."),
	;
	private final int code;
	private final String message;

}

