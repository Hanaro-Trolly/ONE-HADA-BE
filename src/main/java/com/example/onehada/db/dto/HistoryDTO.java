package com.example.onehada.db.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class HistoryDTO {
	private Long history_id;
	private Long user_id;
	private String history_name;
	private LocalDateTime activity_date;
}
