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
        prompt.append("Genera una respuesta en formato JSON usando EXACTAMENTE estas claves, sin renombrarlas ni omitirlas:\n");
        if (types.contains("QUIZ")) {
            prompt.append("""
                - "quiz": {
                    "title": string,
                    "description": string,
                    "questions": [
                      {
                        "statement": string (obligatorio, el enunciado de la pregunta),
                        "explanation": string (opcional),
                        "options": [
                          { "text": string (obligatorio), "correct": boolean (obligatorio) }
                        ]
                      }
                    ]
                  }
                  Cada pregunta debe tener al menos 2 opciones y exactamente una con "correct": true.
                """);
        }
        if (types.contains("FLASHCARD")) {
            prompt.append("""
                - "flashcards": [
                    {
                      "title": string,
                      "flashcards": [
                        { "front": string (obligatorio), "back": string (obligatorio) }
                      ]
                    }
                  ]
                """);
        }
        prompt.append("No uses ningún otro nombre de campo (por ejemplo, no uses \"question\", \"answers\" ni \"isCorrect\"). ");
        prompt.append("Devuelve **únicamente** el JSON, sin texto adicional y sin bloques de código markdown.");
        return prompt.toString();
    }
}