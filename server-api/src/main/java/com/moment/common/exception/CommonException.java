package com.moment.common.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class CommonException extends RuntimeException{
	private final ErrorCode errorCode;
}