package com.upc.learntrack.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StatisticsSnapshotDto {
   private Long id;

   @NotBlank(message = "El código del grupo es obligatorio")
   private String groupCode;

   @NotBlank(message = "El nombre del tema es obligatorio")
   private String topicName;

   private BigDecimal masteryPercentage;
   private Integer studentsCount;
   private LocalDateTime generatedAt;
}