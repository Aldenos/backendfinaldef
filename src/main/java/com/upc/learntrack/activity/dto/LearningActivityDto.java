package com.upc.learntrack.activity.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LearningActivityDto {
   private Long id;

   @NotBlank(message = "El título es obligatorio")
   private String title;

   private String description;
   private String type;
   private String status;
   private Boolean generatedByAi;
   private LocalDateTime createdAt;
   private Boolean personal;
   private Long topicId;
   private String topicName;

   @Valid
   private List<QuestionDto> questions;
}