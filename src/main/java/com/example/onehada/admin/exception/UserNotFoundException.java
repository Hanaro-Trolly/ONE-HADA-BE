package com.example.onehada.admin.exception;

public class UserNotFoundException extends BaseException {
	public UserNotFoundException() {
		super("USER_NOT_FOUND","상담 데이터 추가 실패");
	}
}
