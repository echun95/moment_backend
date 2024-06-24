package com.moment.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.validation.FieldError;

import java.util.List;

@Builder
public record ErrorResponse(@JsonProperty("code") int code,
                            @JsonProperty("message") String message
	, @JsonInclude(JsonInclude.Include.NON_EMPTY) List<ValidationError> errors) {

	@Builder
	public record ValidationError(String field, String message) {
		public static ValidationError of(final FieldError fieldError) {
			return ValidationError.builder()
				.field(fieldError.getField())
				.message(fieldError.getDefaultMessage())
				.build();
		}
		public static ValidationError of(String field, String message) {
			return ValidationError.builder()
				.field(field)
				.message(message)
				.build();
		}
	}
}
