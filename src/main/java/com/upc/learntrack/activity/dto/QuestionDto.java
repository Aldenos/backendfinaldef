package com.upc.learntrack.activity.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class QuestionDto {
   private Long id;

   @NotBlank(message = "El enunciado es obligatorio")
   private String statement;

   private String explanation;
   private Integer orderIdx;

   @Valid
   private List<QuestionOptionDto> options;
}