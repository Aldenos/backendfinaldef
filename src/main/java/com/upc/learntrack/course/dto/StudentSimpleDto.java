package com.upc.learntrack.course.dto;

import lombok.Data;

@Data
public class StudentSimpleDto {
    private Long id;
    private String name;
    private String email;
    private Integer avgScore;
    private Integer completedActivities;
}