package com.upc.learntrack.activity.dto;

import lombok.Data;

@Data
public class FlashcardDto {
    private Long id;
    private String front;
    private String back;
}