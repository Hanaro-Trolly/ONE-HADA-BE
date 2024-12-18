package com.example.onehada.customer.history;

import java.time.LocalDateTime;
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
public class HistoryDTO {
	private Long historyId;
	private Long userId;
	private String historyName;
	private Map<String, Object> historyElements;
	private LocalDateTime activityDate;
}
