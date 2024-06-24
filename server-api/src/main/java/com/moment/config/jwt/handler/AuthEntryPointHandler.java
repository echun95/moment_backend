package com.moment.config.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moment.common.exception.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증되지 않은 사용자가 보안 HTTP 리소스를 요청할 때마다 트리거되고 발생
 * */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthEntryPointHandler implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException)
		throws IOException {
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		ErrorResponse errorResponse;
		if (authException.getCause() instanceof ExpiredJwtException) {
			// 만료된 JWT 예외 처리
			errorResponse = ErrorResponse.builder()
					.code(HttpServletResponse.SC_UNAUTHORIZED)
					.message("로그인 정보가 만료됐습니다. 다시 로그인 해주세요.")
					.build();
		} else if(authException.getCause() instanceof InsufficientAuthenticationException) {
			// 기타 인증 예외 처리
			errorResponse = ErrorResponse.builder()
					.code(HttpServletResponse.SC_UNAUTHORIZED)
					.message("접근 권한이 없는 페이지입니다.")
					.build();
		}else {
			// 기타 인증 예외 처리
			errorResponse = ErrorResponse.builder()
					.code(HttpServletResponse.SC_UNAUTHORIZED)
					.message("인증에 실패했습니다. 다시 로그인 해주세요.")
					.build();
		}

		log.warn("Unauthorized error: {}", authException.getMessage());
		final ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), errorResponse);
	}

}
