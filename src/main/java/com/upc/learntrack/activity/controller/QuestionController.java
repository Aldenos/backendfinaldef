package com.upc.learntrack.activity.controller;

import com.upc.learntrack.activity.dto.QuestionDto;
import com.upc.learntrack.activity.dto.QuestionOptionDto;
import com.upc.learntrack.activity.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/activities/{activityId}/questions")
    @PreAuthorize("hasAnyAuthority('DOCENTE', 'ESTUDIANTE')")
    public ResponseEntity<QuestionDto> createQuestion(
            @PathVariable Long activityId,
            @Valid @RequestBody QuestionDto dto,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionService.createQuestion(activityId, dto, principal.getName()));
    }

    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasAnyAuthority('DOCENTE', 'ESTUDIANTE')")
    public ResponseEntity<QuestionDto> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionDto dto,
            Principal principal) {
        return ResponseEntity.ok(questionService.updateQuestion(questionId, dto, principal.getName()));
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasAnyAuthority('DOCENTE', 'ESTUDIANTE')")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long questionId,
            Principal principal) {
        questionService.deleteQuestion(questionId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/questions/{questionId}/options")
    @PreAuthorize("hasAnyAuthority('DOCENTE', 'ESTUDIANTE')")
    public ResponseEntity<QuestionOptionDto> createOption(
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionOptionDto dto,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionService.createOption(questionId, dto, principal.getName()));
    }

    @PutMapping("/options/{optionId}")
    @PreAuthorize("hasAnyAuthority('DOCENTE', 'ESTUDIANTE')")
    public ResponseEntity<QuestionOptionDto> updateOption(
            @PathVariable Long optionId,
            @Valid @RequestBody QuestionOptionDto dto,
            Principal principal) {
        return ResponseEntity.ok(questionService.updateOption(optionId, dto, principal.getName()));
    }

    @DeleteMapping("/options/{optionId}")
    @PreAuthorize("hasAnyAuthority('DOCENTE', 'ESTUDIANTE')")
    public ResponseEntity<Void> deleteOption(
            @PathVariable Long optionId,
            Principal principal) {
        questionService.deleteOption(optionId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}