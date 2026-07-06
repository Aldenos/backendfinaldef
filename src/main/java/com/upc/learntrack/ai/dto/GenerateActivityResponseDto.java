package com.upc.learntrack.ai.dto;

import com.upc.learntrack.activity.dto.LearningActivityDto;
import com.upc.learntrack.activity.dto.FlashcardSetDto;
import lombok.Data;
import java.util.List;

@Data
public class GenerateActivityResponseDto {
    private LearningActivityDto quiz;
    private List<FlashcardSetDto> flashcards;
}