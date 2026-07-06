package com.upc.learntrack.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningCollectionDto {

   private Long id;
   private String code;

   @NotBlank(message = "El nombre de la colección es obligatorio")
   private String name;

   private String description;
}