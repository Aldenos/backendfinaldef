package com.upc.learntrack.learningpath.controller;

import com.upc.learntrack.learningpath.dto.ConceptualGapDto;
import com.upc.learntrack.learningpath.dto.GroupGapDto;
import com.upc.learntrack.learningpath.dto.SubTopicGapDto;
import com.upc.learntrack.learningpath.service.ConceptualGapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ConceptualGapController {

    private final ConceptualGapService service;

    @GetMapping("/learning-paths/{learningPathId}/gaps")
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE', 'DOCENTE')")
    public ResponseEntity<List<ConceptualGapDto>> getGapsByLearningPath(@PathVariable Long learningPathId) {
        return ResponseEntity.ok(service.findAllByLearningPathId(learningPathId));
    }

    @PatchMapping("/learning-paths/gaps/{gapId}/resolve")
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE', 'DOCENTE')")
    public ResponseEntity<Void> resolve(@PathVariable Long gapId) {
        service.resolveGap(gapId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/groups/{groupCode}/gaps")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<List<GroupGapDto>> getGroupGaps(@PathVariable String groupCode) {
        return ResponseEntity.ok(service.findGroupGaps(groupCode));
    }

    @GetMapping("/groups/{groupCode}/subtopics-gaps")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<List<SubTopicGapDto>> getSubTopicGaps(@PathVariable String groupCode) {
        return ResponseEntity.ok(service.findSubTopicGapsByGroup(groupCode));
    }

    @GetMapping("/learning-paths/{learningPathId}/subtopic-gaps")
    @PreAuthorize("hasAnyAuthority('ESTUDIANTE', 'DOCENTE')")
    public ResponseEntity<List<SubTopicGapDto>> getSubTopicGapsByLearningPath(@PathVariable Long learningPathId) {
        return ResponseEntity.ok(service.findSubTopicGapsByLearningPath(learningPathId));
    }
}