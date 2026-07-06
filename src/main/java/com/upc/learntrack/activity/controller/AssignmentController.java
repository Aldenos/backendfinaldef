package com.upc.learntrack.activity.controller;

import com.upc.learntrack.activity.dto.AssignmentDto;
import com.upc.learntrack.activity.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class AssignmentController {

   private final AssignmentService assignmentService;

   @PostMapping("/{activityId}/assignments")
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<List<AssignmentDto>> assignActivity(
           @PathVariable Long activityId,
           @Valid @RequestBody AssignmentDto dto) {
      return ResponseEntity.status(HttpStatus.CREATED)
              .body(assignmentService.assignToGroups(activityId, dto));
   }
}