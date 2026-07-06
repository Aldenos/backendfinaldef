package com.upc.learntrack.activity.dto;

import lombok.Data;
import java.util.List;

@Data
public class FlashcardSetDto {
    private Long id;
    private String title;
    private Long topicId;
    private String topicName;
    private List<FlashcardDto> flashcards;
}