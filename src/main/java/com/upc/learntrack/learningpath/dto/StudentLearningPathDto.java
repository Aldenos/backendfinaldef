package com.upc.learntrack.learningpath.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StudentLearningPathDto {
    private String studentName;
    private String studentEmail;
    private String collectionName;
    private BigDecimal currentPercentage;
    private String status;
    private Integer completedTopics;
    private Integer totalTopics;
    private Integer completionRate;
}