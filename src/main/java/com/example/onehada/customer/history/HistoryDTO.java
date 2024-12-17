package com.example.onehada.customer.history;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
public class HistoryDTO {
	private Long historyId;
	private Long userId;
	private String historyName;
	private String historyUrl;
	private Map<String, Object> historyElements;
	private LocalDateTime activityDate;
}
