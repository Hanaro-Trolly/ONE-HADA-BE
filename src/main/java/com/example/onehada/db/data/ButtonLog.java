package com.example.onehada.db.data;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "button_logs")
public class ButtonLog {
    @Id
    private String id;
    private String userId;
    private LocalDateTime clickTime;
    private String buttonId;
    private String buttonType;
    private String buttonName;
}
