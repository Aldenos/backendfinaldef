package com.upc.learntrack.activity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class QuestionOptionNotFoundException extends RuntimeException {

    public QuestionOptionNotFoundException(String message) {
        super(message);
    }
}