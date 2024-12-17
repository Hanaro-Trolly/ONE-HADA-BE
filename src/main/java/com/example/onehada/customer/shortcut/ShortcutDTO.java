package com.example.onehada.customer.shortcut;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
	private Map<String, Object> shortcutElements;
	@JsonProperty("isFavorite")
	private boolean isFavorite;
}
