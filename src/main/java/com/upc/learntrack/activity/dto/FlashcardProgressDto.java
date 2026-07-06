package com.upc.learntrack.activity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FlashcardProgressDto {
    @NotNull(message = "Debe indicar si recordó la tarjeta (true/false)")
    private Boolean recalled;
}