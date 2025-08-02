package com.securescope.common.exception;

import com.securescope.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(
		ResourceNotFoundException exception,
		HttpServletRequest request
	) {
		return buildErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(
		BadRequestException exception,
		HttpServletRequest request
	) {
		return buildErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorized(
		UnauthorizedException exception,
		HttpServletRequest request
	) {
		return buildErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(
		MethodArgumentNotValidException exception,
		HttpServletRequest request
	) {
		String message = exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.findFirst()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.orElse("Validation failed");

		return buildErrorResponse(message, request.getRequestURI(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpectedException(
		Exception exception,
		HttpServletRequest request
	) {
		return buildErrorResponse(
			"Something went wrong. Please try again later.",
			request.getRequestURI(),
			HttpStatus.INTERNAL_SERVER_ERROR
		);
	}

	private ResponseEntity<ErrorResponse> buildErrorResponse(
		String message,
		String path,
		HttpStatus status
	) {
		return ResponseEntity.status(status).body(ErrorResponse.of(message, path));
	}
}
