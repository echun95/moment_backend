package com.moment.config.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moment.common.exception.CommonException;
import com.moment.common.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response); // jwtAuthentication 실행
        }catch(Exception e){
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResponse errorResponse;
            if (e instanceof CommonException) {// 정의한 error에 속할 경우
                CommonException customException = (CommonException) e;
                errorResponse = ErrorResponse.builder()
                        .code(customException.getErrorCode().getCode())
                        .message(customException.getErrorCode().getMessage())
                        .build();
            }else{
                errorResponse = ErrorResponse.builder()
                        .code(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                        .message("서버 에러가 발생했습니다. 다시 시도해주세요.")
                        .build();
            }
            log.warn("Unauthorized error: {}", e.getMessage());
            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), errorResponse);
        }
    }
}