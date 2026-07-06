package com.upc.learntrack.course.controller;

import com.upc.learntrack.course.dto.GroupDto;
import com.upc.learntrack.course.service.GroupService;
import com.upc.learntrack.course.dto.GroupStatisticDto;
import com.upc.learntrack.course.dto.LearningCollectionDto;
import com.upc.learntrack.course.dto.TopicStatisticDto;
import com.upc.learntrack.course.service.LearningCollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/collections")
@RequiredArgsConstructor
public class LearningCollectionController {

    private final LearningCollectionService learningCollectionService;
    private final GroupService groupService;

    @GetMapping
    public ResponseEntity<List<LearningCollectionDto>> findAll() {
        return ResponseEntity.ok(learningCollectionService.findAll());
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<List<LearningCollectionDto>> findMyCollections(Principal principal) {
        return ResponseEntity.ok(learningCollectionService.findAllMyCollections(principal.getName()));
    }

    @GetMapping("/enrolled")
    @PreAuthorize("hasAuthority('ESTUDIANTE')")
    public ResponseEntity<List<LearningCollectionDto>> findEnrolledCollections(Principal principal) {
        return ResponseEntity.ok(learningCollectionService.findEnrolledCollections(principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningCollectionDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(learningCollectionService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<LearningCollectionDto> save(@Valid @RequestBody LearningCollectionDto dto, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(learningCollectionService.save(dto, principal.getName()));
    }

    // Estadísticas por colección con filtro de fechas
    @GetMapping("/{collectionName}/statistics")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<List<TopicStatisticDto>> getCollectionStatistics(
            @PathVariable String collectionName,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        return ResponseEntity.ok(learningCollectionService.getStatisticsByCollection(collectionName, startDate, endDate));
    }

    // Comparativa entre grupos con filtro de fechas
    @GetMapping("/{collectionName}/groups-statistics")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<List<GroupStatisticDto>> getGroupsStatistics(
            @PathVariable String collectionName,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        return ResponseEntity.ok(learningCollectionService.getGroupsStatistics(collectionName, startDate, endDate));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<LearningCollectionDto> update(
            @PathVariable Long id,
            @Valid @RequestBody LearningCollectionDto dto,
            Principal principal) {
        return ResponseEntity.ok(learningCollectionService.update(id, dto, principal.getName()));
    }

    @GetMapping("/{collectionId}/groups")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<List<GroupDto>> getGroupsByCollection(
            @PathVariable Long collectionId,
            Principal principal) {
        return ResponseEntity.ok(groupService.findGroupsByCollection(collectionId, principal.getName()));
    }

    @PostMapping("/{collectionId}/groups")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<GroupDto> createGroupInCollection(
            @PathVariable Long collectionId,
            @Valid @RequestBody GroupDto dto,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupService.saveInCollection(collectionId, dto, principal.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<Void> deleteCollection(
            @PathVariable Long id,
            Principal principal) {
        learningCollectionService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}