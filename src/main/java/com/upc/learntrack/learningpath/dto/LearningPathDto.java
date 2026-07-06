package com.upc.learntrack.learningpath.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LearningPathDto {
   private Long id;
   private String collectionName;
   private BigDecimal targetPercentage;
   private BigDecimal currentPercentage;
   private String status;
   private LocalDateTime updatedAt;
   
   private List<PathNodeDto> pathNodes;
   private List<ConceptualGapDto> conceptualGaps;
}