package com.example.onehada.customer.shortcut;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShortcutDTO {
	private Long shortcutId;
	private Long userId;
	private String shortcutName;
	private String shortcutUrl;
	// private ShortcutElementDTO shortcutElements;
	private Map<String, Object> shortcutElements;
	private boolean isFavorite;
}