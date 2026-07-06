package com.upc.learntrack.activity.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssignmentDto {
   private Long id;
   private String groupCode;

   @NotEmpty(message = "Debe seleccionar al menos un grupo")
   private List<String> groupCodes;

   private LocalDateTime dueDate;
   private String activityTitle;
}