package com.example.onehada.db.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class ShortcutDTO {
	private Long shortcut_id;
	private Long user_id;
	private Long history_id;
	private String history_elements;
	private String shortcut_name;
}
