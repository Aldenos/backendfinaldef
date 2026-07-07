package com.upc.learntrack.ai.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.learntrack.activity.dto.CreateFlashcardDto;
import com.upc.learntrack.activity.dto.CreateFlashcardSetDto;
import com.upc.learntrack.activity.dto.FlashcardSetDto;
import com.upc.learntrack.activity.dto.LearningActivityDto;
import com.upc.learntrack.activity.service.FlashcardService;
import com.upc.learntrack.activity.service.LearningActivityService;
import com.upc.learntrack.ai.dto.GenerateActivityResponseDto;
import com.upc.learntrack.ai.exception.AiGenerationException;
import com.upc.learntrack.ai.service.AiGeneratorService;
import com.upc.learntrack.ai.service.PromptBuilderService;
import com.upc.learntrack.course.exception.TopicNotFoundException;
import com.upc.learntrack.course.model.Topic;
import com.upc.learntrack.course.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiGeneratorServiceImpl implements AiGeneratorService {

    private final LearningActivityService learningActivityService;
    private final FlashcardService flashcardService;
    private final TopicRepository topicRepository;
    private final ObjectMapper objectMapper;
    private final ChatModel chatModel;
    private final PromptBuilderService promptBuilder;

    @Override
    @Transactional
    public GenerateActivityResponseDto generateActivity(String topicName, String content, String userEmail, List<String> types) {
        try {
            Topic topic = topicRepository.findByName(topicName)
                    .orElseThrow(() -> new TopicNotFoundException("Tema no encontrado: " + topicName));

            // 1. Construir el prompt
            String prompt = promptBuilder.buildMultiFormatPrompt(topicName, content, types);
            log.info("Prompt enviado a IA: {}", prompt);

            // 2. Llamar a Groq via Spring AI
            String response = chatModel.call(prompt);
            log.info("Respuesta recibida de IA: {}", response);

            // 3. Parsear el JSON que devolvió la IA (algunos modelos envuelven la respuesta en ```json ... ``` a pesar de pedirle solo JSON)
            JsonNode root = objectMapper.readTree(stripCodeFences(response));
            GenerateActivityResponseDto result = new GenerateActivityResponseDto();

            if (types.contains("QUIZ")) {
                LearningActivityDto quiz = objectMapper.treeToValue(root.get("quiz"), LearningActivityDto.class);
                quiz.setGeneratedByAi(true);
                quiz.setStatus("DRAFT");
                LearningActivityDto savedQuiz = learningActivityService.save(topic.getId(), quiz, userEmail);
                result.setQuiz(savedQuiz);
            }

            if (types.contains("FLASHCARD")) {
                List<FlashcardSetDto> flashcardSets = objectMapper.readValue(
                        root.get("flashcards").traverse(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, FlashcardSetDto.class));

                for (FlashcardSetDto setDto : flashcardSets) {
                    CreateFlashcardSetDto createDto = new CreateFlashcardSetDto();
                    createDto.setTitle(setDto.getTitle());
                    if (setDto.getFlashcards() != null) {
                        List<CreateFlashcardDto> cards = setDto.getFlashcards().stream()
                                .map(f -> {
                                    CreateFlashcardDto card = new CreateFlashcardDto();
                                    card.setFront(f.getFront());
                                    card.setBack(f.getBack());
                                    return card;
                                }).toList();
                        createDto.setFlashcards(cards);
                    }
                    flashcardService.createFlashcardSet(topic.getId(), createDto, userEmail);
                }
                result.setFlashcards(flashcardSets);
            }

            return result;

        } catch (Exception e) {
            log.error("Error al generar actividad con IA", e);
            throw new AiGenerationException("Error al generar la actividad con IA: " + e.getMessage(), e);
        }
    }

    private String stripCodeFences(String response) {
        String trimmed = response.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
            if (trimmed.startsWith("json")) {
                trimmed = trimmed.substring(4);
            }
            int closingFence = trimmed.lastIndexOf("```");
            if (closingFence >= 0) {
                trimmed = trimmed.substring(0, closingFence);
            }
        }
        return trimmed.trim();
    }
}