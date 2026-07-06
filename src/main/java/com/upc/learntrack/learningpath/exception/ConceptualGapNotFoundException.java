package com.upc.learntrack.learningpath.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConceptualGapNotFoundException extends RuntimeException {
    public ConceptualGapNotFoundException(String message) { super(message); }
}