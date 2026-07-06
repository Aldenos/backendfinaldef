package com.upc.learntrack.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PdfReportDto {
    private Long id;

    @NotBlank(message = "El código del grupo es obligatorio")
    private String groupCode;

    @NotBlank(message = "El nombre de la colección es obligatorio")
    private String collectionName;

    private String fileUrl;
    
    private String optionalEmail; 

    private String sentTo;
    private LocalDateTime createdAt;
}