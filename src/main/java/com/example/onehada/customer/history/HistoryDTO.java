package com.example.onehada.customer.history;

import java.time.LocalDateTime;

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
	private Long historyID;
	private Long userId;
	private String historyName;
	private String historyUrl;
	private LocalDateTime activityDate;
}
