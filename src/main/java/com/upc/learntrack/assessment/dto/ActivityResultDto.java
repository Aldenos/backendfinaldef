package com.upc.learntrack.assessment.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ActivityResultDto {
   private Long id;
   private String activityTitle;
   private String studentEmail;
   private BigDecimal score;
   private Integer totalQuestions;
   private Integer correctAnswers;
   private Integer timeSpentSeconds;
   private String status;
   private LocalDateTime completedAt;
}