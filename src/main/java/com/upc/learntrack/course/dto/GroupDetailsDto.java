package com.upc.learntrack.course.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GroupDetailsDto {
    private Long id;
    private String name;
    private String code;
    private Integer studentsCount;
    private Integer activitiesCount;
    private LocalDateTime createdAt;
}