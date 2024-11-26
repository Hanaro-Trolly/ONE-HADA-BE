package com.example.onehada.websocket.service;

import com.example.onehada.websocket.model.ButtonClickEvent;
import org.springframework.stereotype.Service;

@Service
public class ButtonLogService {

	public ButtonClickEvent processButtonClick(ButtonClickEvent event) {
		// 여기서 필요한 경우 로그를 데이터베이스에 저장하거나 추가 처리를 할 수 있습니다
		return event;
	}
}
