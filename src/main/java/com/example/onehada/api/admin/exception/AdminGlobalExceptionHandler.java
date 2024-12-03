package com.example.onehada.api.admin.exception;

import com.example.onehada.db.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AdminGlobalExceptionHandler {

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ApiResponse> handleBaseException(BaseException e) {
		return ResponseEntity.badRequest()
			.body(new ApiResponse(400, e.getCode(), e.getMessage(), null));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse> handleException(Exception e) {
		return ResponseEntity.internalServerError()
			.body(new ApiResponse(500, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.", null));
	}
}
