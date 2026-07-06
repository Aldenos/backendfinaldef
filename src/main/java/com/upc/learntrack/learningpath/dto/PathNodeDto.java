package com.upc.learntrack.learningpath.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PathNodeDto {
   private Long id;
   private String topicName;
   private Integer orderIdx;
   private Boolean completed;
   private BigDecimal masteryScore;
}