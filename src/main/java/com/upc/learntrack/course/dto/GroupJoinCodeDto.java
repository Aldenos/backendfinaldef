package com.upc.learntrack.course.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupJoinCodeDto {
    private String code;
    private String groupCode;
    private LocalDateTime expiresAt;
}