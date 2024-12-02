package com.example.onehada.api.admin.exception;

public class UserNotFoundException extends Exception {
	public UserNotFoundException() {
		super("USER_NOT_FOUND");
	}
}
