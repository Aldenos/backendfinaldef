package com.upc.learntrack.learningpath.dto;

import lombok.Data;

@Data
public class ConceptualGapDto {
   private Long id;
   private String topicName;
   private String description;
   private Boolean resolved;
}