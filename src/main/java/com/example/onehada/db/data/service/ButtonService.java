package com.example.onehada.db.data.service;

import com.example.onehada.db.data.Button;
import com.example.onehada.db.data.ButtonLog;
import com.example.onehada.db.data.ButtonSession;
import com.example.onehada.db.data.repository.ButtonRepository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ButtonService {


    private final MongoTemplate mongoTemplate;

    private final RecommendService recommendService;  // RecommendService 주입
    private final ButtonRepository buttonRepository;

    public ButtonService(MongoTemplate mongoTemplate, RecommendService recommendService, ButtonRepository buttonRepository) {
        this.mongoTemplate = mongoTemplate;
        this.recommendService = recommendService;
        this.buttonRepository = buttonRepository;
    }

    public void saveButtonLog(String userId, String buttonId) {
        // 버튼 정보 조회
        Button button = getButtonByName(buttonId);

        // 버튼 로그 생성
        ButtonLog buttonLog = ButtonLog.builder()
                .userId(userId)
                .buttonId(buttonId)
                .buttonName(button.getName())
                .buttonType(button.getType())  // 버튼의 타입 저장
                .clickTime(LocalDateTime.now()) // 현재 시간 저장
                .build();

        // MongoDB에 로그 저장
        mongoTemplate.save(buttonLog);
    }

    public Button getButtonById(String buttonId) {
        // buttonId로 버튼 정보 조회
        return mongoTemplate.findById(buttonId, Button.class);
    }

    public Button getButtonByName(String buttonName) {
        return buttonRepository.findByName(buttonName);
    }

    public Button saveButton(Button button) {
        return buttonRepository.save(button);
    }

    public ButtonSession processUserClickHistory(String userId) {
        ButtonLog lastProductClick = mongoTemplate.findOne(
            Query.query(Criteria.where("userId").is(userId)
                .and("buttonType").is("product"))
                .with(Sort.by(Sort.Direction.DESC, "clickTime"))
                .limit(1),
            ButtonLog.class
        );
        // System.out.println("product: "+lastProductClick.getButtonId());
    
        if (lastProductClick == null) return null;
    
        // 2. 가장 최근의 start 버튼 찾기
        ButtonLog startButton = mongoTemplate.findOne(
            Query.query(Criteria.where("userId").is(userId)
                .and("buttonType").is("start")
                .and("clickTime").lt(lastProductClick.getClickTime()))
                .with(Sort.by(Sort.Direction.DESC, "clickTime"))
                .limit(1),
            ButtonLog.class
        );
        // System.out.println("start: "+startButton.getButtonId());

        if (startButton == null) return null;
    
        // 3. 세션 생성 및 추천 관계 추가
        ButtonSession latestSession = new ButtonSession(startButton, lastProductClick);
        recommendService.addRecommend(
            startButton.getButtonName(),
            lastProductClick.getButtonName()
        );
    
        return latestSession;
    }
}
