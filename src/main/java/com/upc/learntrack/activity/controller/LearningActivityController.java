package com.upc.learntrack.activity.controller;

import com.upc.learntrack.activity.dto.LearningActivityDto;
import com.upc.learntrack.activity.service.LearningActivityService;
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
public class LearningActivityController {

    private final LearningActivityService learningActivityService;

    @PostMapping("/topics/{topicId}/activities")
    @PreAuthorize("hasAnyAuthority('DOCENTE', 'ESTUDIANTE')")
    public ResponseEntity<LearningActivityDto> save(
            @PathVariable Long topicId,
            @Valid @RequestBody LearningActivityDto dto,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(learningActivityService.save(topicId, dto, principal.getName()));
    }

    @PutMapping("/activities/{id}")
    @PreAuthorize("hasAnyAuthority('DOCENTE', 'ESTUDIANTE')")
    public ResponseEntity<LearningActivityDto> update(
            @PathVariable Long id,
            @Valid @RequestBody LearningActivityDto dto,
            Principal principal) {
        return ResponseEntity.ok(learningActivityService.update(id, dto, principal.getName()));
    }

    @DeleteMapping("/activities/{id}")
    @PreAuthorize("hasAnyAuthority('DOCENTE', 'ESTUDIANTE')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        learningActivityService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/activities/{id}")
    @PreAuthorize("hasAnyAuthority('DOCENTE', 'ESTUDIANTE')")
    public ResponseEntity<LearningActivityDto> getActivityById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(learningActivityService.findByIdForStudent(id, principal.getName()));
    }

    @GetMapping("/activities/pending")
    @PreAuthorize("hasAuthority('ESTUDIANTE')")
    public ResponseEntity<List<LearningActivityDto>> getPendingActivities(Principal principal) {
        return ResponseEntity.ok(learningActivityService.findPendingActivities(principal.getName()));
    }

    @GetMapping("/topics/{topicId}/activities")
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE', 'DOCENTE')")
    public ResponseEntity<List<LearningActivityDto>> getActivitiesByTopic(@PathVariable Long topicId) {
        return ResponseEntity.ok(learningActivityService.findByTopicId(topicId));
    }

    @GetMapping("/my-resources")
    @PreAuthorize("hasAuthority('ESTUDIANTE')")
    public ResponseEntity<List<LearningActivityDto>> getMyPersonalActivities(Principal principal) {
        return ResponseEntity.ok(learningActivityService.findMyPersonalResources(principal.getName()));
    }
}