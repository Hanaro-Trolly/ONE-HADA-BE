package com.example.onehada.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ActivityLogResponseDTO {
	private Long userId;
	private String userName;
	private List<ActivityLogDetailDTO> logs;
}
