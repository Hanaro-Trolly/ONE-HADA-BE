package com.example.onehada.admin.exception;

public class InvalidCredentialsException extends BaseException {
	public InvalidCredentialsException() {
		super("BAD_REQUEST", "이메일 또는 비밀번호가 잘못되었습니다.");
	}
}
