package com.upc.learntrack.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDto {
   private Long id;

   @NotBlank(message = "El nombre del grupo es obligatorio")
   private String name;

   @NotBlank(message = "El código es obligatorio")
   private String code;

   private Long collectionId;
   private String collectionName;

   private LocalDateTime createdAt;
}