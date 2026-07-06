package com.upc.learntrack.learningpath.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SubTopicGapDto {
    private String subTopicName;
    private String topicName;
    private BigDecimal errorRate;
    private Integer totalQuestions;
    private Integer incorrectAnswers;
}