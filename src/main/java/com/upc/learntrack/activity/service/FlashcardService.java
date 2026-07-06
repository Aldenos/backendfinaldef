package com.upc.learntrack.activity.service;

import com.upc.learntrack.activity.dto.CreateFlashcardDto;
import com.upc.learntrack.activity.dto.FlashcardSetDto;
import com.upc.learntrack.activity.dto.CreateFlashcardSetDto;
import java.util.List;

public interface FlashcardService {
    FlashcardSetDto getFlashcardSetByTopic(Long topicId);
    void recordProgress(Long flashcardId, Boolean recalled, String studentEmail);
    List<FlashcardSetDto> getAllFlashcardSets(String studentEmail);
    FlashcardSetDto createFlashcardSet(Long topicId, CreateFlashcardSetDto dto, String userEmail);
    FlashcardSetDto addFlashcardToSet(Long setId, CreateFlashcardDto dto, String userEmail);
    void deleteFlashcard(Long flashcardId, String userEmail);
    List<FlashcardSetDto> getFlashcardSetsByTopic(Long topicId);
}