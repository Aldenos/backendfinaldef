package com.upc.learntrack.activity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class CreateFlashcardSetDto {
    @NotBlank
    private String title;
    private List<CreateFlashcardDto> flashcards;
}