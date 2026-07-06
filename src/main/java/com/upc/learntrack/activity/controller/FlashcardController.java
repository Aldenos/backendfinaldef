package com.upc.learntrack.activity.controller;

import com.upc.learntrack.activity.dto.CreateFlashcardDto;
import com.upc.learntrack.activity.dto.CreateFlashcardSetDto;
import com.upc.learntrack.activity.dto.FlashcardProgressDto;
import com.upc.learntrack.activity.dto.FlashcardSetDto;
import com.upc.learntrack.activity.service.FlashcardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FlashcardController {

    private final FlashcardService flashcardService;

    @GetMapping("/topics/{topicId}/flashcards")
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE', 'DOCENTE')")
    public ResponseEntity<FlashcardSetDto> getFlashcardsByTopic(@PathVariable Long topicId) {
        return ResponseEntity.ok(flashcardService.getFlashcardSetByTopic(topicId));
    }

    // Nuevo endpoint: listar todos los sets de flashcards de un tema
    @GetMapping("/topics/{topicId}/flashcard-sets")
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE', 'DOCENTE')")
    public ResponseEntity<List<FlashcardSetDto>> getFlashcardSetsByTopic(@PathVariable Long topicId) {
        return ResponseEntity.ok(flashcardService.getFlashcardSetsByTopic(topicId));
    }

    @PostMapping("/flashcards/{flashcardId}/recall")
    @PreAuthorize("hasAuthority('ESTUDIANTE')")
    public ResponseEntity<Void> recordRecall(
            @PathVariable Long flashcardId,
            @Valid @RequestBody FlashcardProgressDto dto,
            Principal principal) {
        flashcardService.recordProgress(flashcardId, dto.getRecalled(), principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/topics/{topicId}/flashcard-sets")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<FlashcardSetDto> createFlashcardSet(
            @PathVariable Long topicId,
            @Valid @RequestBody CreateFlashcardSetDto dto,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(flashcardService.createFlashcardSet(topicId, dto, principal.getName()));
    }

    @PostMapping("/flashcard-sets/{setId}/flashcards")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<FlashcardSetDto> addFlashcardToSet(
            @PathVariable Long setId,
            @Valid @RequestBody CreateFlashcardDto dto,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(flashcardService.addFlashcardToSet(setId, dto, principal.getName()));
    }

    @DeleteMapping("/flashcards/{flashcardId}")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<Void> deleteFlashcard(@PathVariable Long flashcardId, Principal principal) {
        flashcardService.deleteFlashcard(flashcardId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}