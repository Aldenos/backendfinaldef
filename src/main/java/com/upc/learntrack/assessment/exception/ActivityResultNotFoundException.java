package com.upc.learntrack.assessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ActivityResultNotFoundException extends RuntimeException {
    public ActivityResultNotFoundException(String message) {
        super(message);
    }
}