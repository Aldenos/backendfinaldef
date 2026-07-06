package com.upc.learntrack.ai.service;

import com.upc.learntrack.ai.dto.GenerateActivityResponseDto;
import java.util.List;

public interface AiGeneratorService {
    GenerateActivityResponseDto generateActivity(String topicName, String content, String userEmail, List<String> types);
}