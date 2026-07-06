package com.upc.learntrack.course.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class GroupStatisticDto {
    private String groupCode;
    private String groupName;
    private Map<String, BigDecimal> topicAverageMap;
}