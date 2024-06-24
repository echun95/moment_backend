package com.moment.config.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moment.common.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {


	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {
		log.warn("Unauthorized error: {}", authException.getMessage());

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		ErrorResponse errorResponse = ErrorResponse.builder()
			.code(HttpServletResponse.SC_UNAUTHORIZED)
			.message("사용자 인증에 실패하였습니다.")
			.build();

		final ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), errorResponse);
	}
}
