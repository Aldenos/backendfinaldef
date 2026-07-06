package com.upc.learntrack.ai.service.impl;

import com.upc.learntrack.ai.service.PromptBuilderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromptBuilderServiceImpl implements PromptBuilderService {

    @Override
    public String buildMultiFormatPrompt(String topicName, String content, List<String> types) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Actúa como un experto en educación. Basado en el siguiente contenido sobre el tema '")
                .append(topicName).append("':\n\n").append(content).append("\n\n");
        prompt.append("Genera una respuesta en formato JSON con los siguientes campos:\n");
        if (types.contains("QUIZ")) {
            prompt.append("- \"quiz\": un objeto con la estructura exacta de LearningActivityDto (incluyendo preguntas y opciones).\n");
        }
        if (types.contains("FLASHCARD")) {
            prompt.append("- \"flashcards\": un array de objetos FlashcardSetDto, cada uno con título y lista de flashcards (cada flashcard con front y back).\n");
        }
        prompt.append("Devuelve **únicamente** el JSON, sin texto adicional.");
        return prompt.toString();
    }
}