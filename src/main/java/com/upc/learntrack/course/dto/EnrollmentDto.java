package com.upc.learntrack.course.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EnrollmentDto {
    private Long id;
    
    @NotBlank(message = "El email del estudiante es obligatorio")
    @Email
    private String studentEmail;
    private String groupCode;
}