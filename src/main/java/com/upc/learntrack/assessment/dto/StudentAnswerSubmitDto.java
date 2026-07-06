package com.upc.learntrack.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StudentAnswerSubmitDto {
   @NotBlank(message = "El texto de la pregunta es obligatorio")
   private String questionText;

   @NotBlank(message = "Debes indicar la opción que elegiste")
   private String selectedOptionText;
}