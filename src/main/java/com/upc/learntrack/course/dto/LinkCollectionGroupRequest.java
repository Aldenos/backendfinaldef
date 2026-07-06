package com.upc.learntrack.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LinkCollectionGroupRequest {
    @NotBlank(message = "El nombre de la colección es obligatorio")
    private String collectionName;

    @NotBlank(message = "El nombre del grupo es obligatorio")
    private String groupName;
}