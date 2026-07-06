package com.upc.learntrack.assessment.dto;

import lombok.Data;

@Data
public class AnswerDetailDto {
    private String questionText;
    private String selectedOptionText;
    private String correctOptionText;
    private String explanation;
    private Boolean isCorrect;
}