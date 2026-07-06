package com.upc.learntrack.assessment.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TimelinePointDto {
    private LocalDateTime date;
    private BigDecimal score;
    private String activityTitle;
    private String topicName;
}