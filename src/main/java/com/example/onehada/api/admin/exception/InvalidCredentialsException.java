package com.example.onehada.api.admin.exception;

public class InvalidCredentialsException extends Exception {
	public InvalidCredentialsException() {
		super("이메일 또는 비밀번호가 잘못되었습니다.");
	}
}
