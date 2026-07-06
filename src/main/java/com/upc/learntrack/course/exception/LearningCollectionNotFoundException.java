package com.upc.learntrack.course.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LearningCollectionNotFoundException extends RuntimeException {
    public LearningCollectionNotFoundException(String message) {
        super(message);
    }
}