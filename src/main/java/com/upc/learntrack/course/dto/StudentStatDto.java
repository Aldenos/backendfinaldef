package com.upc.learntrack.course.dto;

import lombok.Data;

@Data
public class StudentStatDto {
    private String name;
    private String email;
    private Integer score;
    private Integer completed;
}