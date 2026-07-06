package com.upc.learntrack.course.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TopicStatisticDto {
    private String topicName;
    private BigDecimal averageMastery;
    private Integer totalSubmissions;
}