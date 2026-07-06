package com.upc.learntrack.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiRequestDto {
    @NotBlank(message = "El tema es obligatorio")
    private String topicName;

    @NotBlank(message = "El contenido para generar la actividad es obligatorio")
    private String content;
}