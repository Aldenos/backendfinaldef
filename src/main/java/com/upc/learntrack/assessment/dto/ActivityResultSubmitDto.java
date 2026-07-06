package com.upc.learntrack.assessment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class ActivityResultSubmitDto {
   private Integer timeSpentSeconds;

   @Valid
   @NotEmpty(message = "Debes enviar al menos una respuesta")
   private List<StudentAnswerSubmitDto> answers;
}