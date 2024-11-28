package com.example.onehada.db.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {
	private int code;
	private String status;
	private String message;
	private Object data;
}
