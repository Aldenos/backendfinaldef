package com.upc.learntrack.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class FeynmanCheckResponseDto {
    private List<KeyPointCheckDto> checks;
    private String feedback;
    private boolean passed;
}
