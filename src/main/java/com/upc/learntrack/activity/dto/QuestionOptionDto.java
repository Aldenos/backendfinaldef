package com.upc.learntrack.activity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionOptionDto {
   private Long id;

   @NotBlank(message = "El texto de la opción es obligatorio")
   private String text;

   @NotNull(message = "Debes indicar si es la opción correcta (true/false)")
   private Boolean correct;
}