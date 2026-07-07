package com.upc.learntrack.assessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ActivityResultSubmissionException extends RuntimeException {
    public ActivityResultSubmissionException(String message, Throwable cause) {
        super(message, cause);
    }
}
