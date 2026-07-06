package com.upc.learntrack.activity.service;

import com.upc.learntrack.activity.dto.QuestionDto;
import com.upc.learntrack.activity.dto.QuestionOptionDto;

public interface QuestionService {
    QuestionDto createQuestion(Long activityId, QuestionDto dto, String userEmail);
    QuestionDto updateQuestion(Long questionId, QuestionDto dto, String userEmail);
    void deleteQuestion(Long questionId, String userEmail);
    QuestionOptionDto createOption(Long questionId, QuestionOptionDto dto, String userEmail);
    QuestionOptionDto updateOption(Long optionId, QuestionOptionDto dto, String userEmail);
    void deleteOption(Long optionId, String userEmail);
}