package com.upc.learntrack.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeynmanCheckRequestDto {

    @NotNull(message = "El tema es obligatorio")
    private Long topicId;

    @NotBlank(message = "La explicación no puede estar vacía")
    private String explanation;
}
