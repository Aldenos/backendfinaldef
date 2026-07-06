package com.upc.learntrack.assessment.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivityResultDetailDto {
    private Long id;
    private String activityTitle;
    private String studentEmail;
    private BigDecimal score;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer timeSpentSeconds;
    private String status;
    private LocalDateTime completedAt;
    private List<AnswerDetailDto> answers;
}