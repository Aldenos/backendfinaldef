package com.upc.learntrack.activity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateFlashcardDto {
    @NotBlank
    private String front;
    @NotBlank
    private String back;
}