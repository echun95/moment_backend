package com.moment.common.exception.handler;

import com.moment.common.exception.CommonErrorCode;
import com.moment.common.exception.ErrorCode;
import com.moment.common.exception.ErrorResponse;
import com.moment.common.exception.RestApiException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleCustomException(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        String target = e.getTarget();
        if (!target.isBlank()) {
            String msg = String.format(errorCode.getMessage(), target);
            log.error("restApiException :" + msg);
            return customExceptionInternal(errorCode, msg);
        }
        log.error("restApiException :" + errorCode.getMessage());
        return customExceptionInternal(errorCode);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAllException(Exception ex) {
        log.error("handleAllException", ex);
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        return allExceptionInternal(errorCode);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleMethodArgumentNotValid(ConstraintViolationException ex) {
        log.error("handleIllegalArgument", ex);
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return customExceptionInternal(ex, errorCode);
    }

    private ResponseEntity<Object> customExceptionInternal(ConstraintViolationException e, ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getCode())
                .body(makeErrorResponse(e, errorCode));
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
            WebRequest request) {
        log.error("handleIllegalArgument", ex);
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return customExceptionInternal(ex, errorCode);
    }

    private ResponseEntity<Object> customExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(makeErrorResponse(errorCode));
    }

    private ResponseEntity<Object> customExceptionInternal(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getCode())
                .body(makeErrorResponse(errorCode, message));
    }

    private ResponseEntity<Object> allExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getCode())
                .body(makeErrorResponse(errorCode));
    }

    private ResponseEntity<Object> customExceptionInternal(BindException e, ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getCode())
                .body(makeErrorResponse(e, errorCode));
    }

    private ErrorResponse makeErrorResponse(final ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(message)
                .build();
    }

    private ErrorResponse makeErrorResponse(BindException e, ErrorCode errorCode) {
        List<ErrorResponse.ValidationError> errorList = e.getBindingResult().getFieldErrors()
                .stream()
                .map(ErrorResponse.ValidationError::of)
                .collect(Collectors.toList());
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .errors(errorList)
                .build();
    }

    private Object makeErrorResponse(ConstraintViolationException e, ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(e.getMessage())
                .build();
    }
}
