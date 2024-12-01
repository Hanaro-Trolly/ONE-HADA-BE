package com.example.onehada.api.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
@AllArgsConstructor
public class ActivityLogResponse {
	private String userId;
	private String user_name;
	private List<ActivityLogDetail> logs;
}
