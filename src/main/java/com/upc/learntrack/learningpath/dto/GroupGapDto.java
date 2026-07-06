package com.upc.learntrack.learningpath.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GroupGapDto {
    private String topicName;
    private BigDecimal averageMastery;      // porcentaje promedio del grupo en ese tema
    private Integer studentsBelowThreshold; // cantidad de estudiantes con dominio < 70%
    private String recommendation;          // "Reenseñar", "Reforzar", etc.
}