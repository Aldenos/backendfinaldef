package com.upc.learntrack.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicDto {
   private Long id;

   @NotBlank(message = "El nombre del tema es obligatorio")
   private String name;

   private Integer orderIdx;
}