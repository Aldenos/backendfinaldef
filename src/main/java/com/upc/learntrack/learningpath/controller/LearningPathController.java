package com.upc.learntrack.learningpath.controller;

import com.upc.learntrack.learningpath.dto.LearningPathDto;
import com.upc.learntrack.learningpath.dto.StudentLearningPathDto;
import com.upc.learntrack.learningpath.service.LearningPathService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LearningPathController {

   private final LearningPathService learningPathService;

   @GetMapping("/learning-paths/mine")
   @PreAuthorize("hasAuthority('ESTUDIANTE')")
   public ResponseEntity<List<LearningPathDto>> findMyPaths(Principal principal) {
      return ResponseEntity.ok(learningPathService.findAllByStudentEmail(principal.getName()));
   }

   @GetMapping("/collections/{collectionName}/learning-path")
   @PreAuthorize("hasAuthority('ESTUDIANTE')")
   public ResponseEntity<LearningPathDto> getAdaptivePath(@PathVariable String collectionName, Principal principal) {
      return ResponseEntity.ok(learningPathService.getAdaptivePath(collectionName, principal.getName()));
   }

   @GetMapping("/topics/{topicId}/learning-path")
   @PreAuthorize("hasAuthority('ESTUDIANTE')")
   public ResponseEntity<LearningPathDto> getAdaptivePathForTopic(@PathVariable Long topicId, Principal principal) {
      return ResponseEntity.ok(learningPathService.getAdaptivePathForTopic(topicId, principal.getName()));
   }

   @GetMapping("/groups/{groupCode}/learning-paths")
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<List<StudentLearningPathDto>> getGroupLearningPaths(@PathVariable String groupCode) {
      return ResponseEntity.ok(learningPathService.findLearningPathsByGroupCode(groupCode));
   }

   @GetMapping("/groups/{groupCode}/learning-path")
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<List<StudentLearningPathDto>> getGroupLearningPathSingular(@PathVariable String groupCode) {
      return ResponseEntity.ok(learningPathService.findLearningPathsByGroupCode(groupCode));
   }
}