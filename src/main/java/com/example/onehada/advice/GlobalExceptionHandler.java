package com.example.onehada.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.onehada.api.admin.exception.BaseException;
import com.example.onehada.db.dto.ApiResponse;
import com.example.onehada.exception.BadRequestException;
import com.example.onehada.exception.ForbiddenException;
import com.example.onehada.exception.NotFoundException;
import com.example.onehada.exception.UnauthorizedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
		// return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(404, "NOT_FOUND", e.getMessage(),
			null));
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(401, "UNAUTHORIZED", e.getMessage(), null));
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<?> handleForbiddenException(ForbiddenException e) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(403, "FORBIDDEN", e.getMessage(), null));
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<?> handleBadRequestException(BadRequestException e) {
		return ResponseEntity.badRequest().body(new ApiResponse(400, "BAD_REQUEST", e.getMessage(), null));
	}

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ApiResponse> handleBaseException(BaseException e) {
		return ResponseEntity.badRequest()
			.body(new ApiResponse(400, e.getCode(), e.getMessage(), null));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleAllExceptions(Exception e) {
		return ResponseEntity.internalServerError().body(new ApiResponse(500, "INTERNAL_SERVER_ERROR", e.getMessage(), null));
	}

}
