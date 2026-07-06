package com.upc.learntrack.assessment.controller;

import com.upc.learntrack.assessment.dto.*;
import com.upc.learntrack.assessment.service.ActivityResultService;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ActivityResultController {

   private final ActivityResultService service;

   @GetMapping("/results/mine")
   @PreAuthorize("hasAuthority('ESTUDIANTE')")
   public ResponseEntity<List<ActivityResultDto>> findMyResults(
           @RequestParam(required = false) String type,
           @RequestParam(required = false) Long topicId,
           @RequestParam(required = false) String sort,
           Principal principal) {
      return ResponseEntity.ok(service.findAllMyResults(principal.getName(), type, topicId, sort));
   }

   @PostMapping("/topics/{topicName}/activities/{activityTitle}/submit")
   @PreAuthorize("hasAuthority('ESTUDIANTE')")
   public ResponseEntity<ActivityResultDetailedResponseDto> submit(
           @PathVariable String topicName,
           @PathVariable String activityTitle,
           @Valid @RequestBody ActivityResultSubmitDto dto,
           Principal principal) {
      return ResponseEntity.status(HttpStatus.CREATED)
              .body(service.submit(topicName, activityTitle, dto, principal.getName()));
   }

   @GetMapping("/results/{resultId}")
   @PreAuthorize("hasAnyAuthority('ESTUDIANTE', 'DOCENTE')")
   public ResponseEntity<ActivityResultDetailDto> getResultDetail(@PathVariable Long resultId, Principal principal) {
      return ResponseEntity.ok(service.findResultDetail(resultId, principal.getName()));
   }

   @GetMapping("/results/mine/timeline")
   @PreAuthorize("hasAuthority('ESTUDIANTE')")
   public ResponseEntity<List<TimelinePointDto>> getMyTimeline(
           @RequestParam(required = false) Long topicId,
           @RequestParam(required = false) LocalDate startDate,
           @RequestParam(required = false) LocalDate endDate,
           Principal principal) {
      return ResponseEntity.ok(service.getTimeline(principal.getName(), topicId, startDate, endDate));
   }
}