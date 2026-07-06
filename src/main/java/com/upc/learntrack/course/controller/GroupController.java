package com.upc.learntrack.course.controller;

import com.upc.learntrack.course.dto.*;
import com.upc.learntrack.course.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {

   private final GroupService groupService;

   @GetMapping("/mine")
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<List<GroupDto>> findMyGroups(Principal principal) {
      return ResponseEntity.ok(groupService.findAllMyGroups(principal.getName()));
   }

   @PostMapping
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<GroupDto> save(@Valid @RequestBody GroupDto dto, Principal principal) {
      return ResponseEntity.status(HttpStatus.CREATED).body(groupService.save(dto, principal.getName()));
   }

   @GetMapping("/{groupId}/students")
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<List<StudentDto>> getGroupStudents(@PathVariable Long groupId) {
      return ResponseEntity.ok(groupService.findStudentsByGroupId(groupId));
   }

   @PatchMapping("/{groupId}/topics/{topicId}/priority")
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<Void> setTopicPriority(@PathVariable Long groupId, @PathVariable Long topicId, @RequestParam boolean priority) {
      groupService.setTopicPriority(groupId, topicId, priority);
      return ResponseEntity.noContent().build();
   }

   @PostMapping("/{groupCode}/enroll")
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<List<StudentDto>> enrollStudents(
           @PathVariable String groupCode,
           @RequestBody List<String> studentEmails,
           Principal principal) {
      List<StudentDto> enrolled = groupService.enrollStudents(groupCode, studentEmails, principal.getName());
      return ResponseEntity.status(HttpStatus.CREATED).body(enrolled);
   }

   // ========== NUEVOS ENDPOINTS REQUERIDOS POR EL FRONTEND ==========

   @GetMapping("/{code}")
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<GroupDetailsDto> getGroupByCode(@PathVariable String code) {
      return ResponseEntity.ok(groupService.findByCode(code));
   }

   @GetMapping("/{code}/students")
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<List<StudentSimpleDto>> getStudentsByGroupCode(@PathVariable String code) {
      return ResponseEntity.ok(groupService.findStudentsByGroupCode(code));
   }

   @PostMapping("/{code}/enroll-student")
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<StudentSimpleDto> enrollSingleStudent(
           @PathVariable String code,
           @RequestBody Map<String, String> payload,
           Principal principal) {
      String email = payload.get("email");
      return ResponseEntity.status(HttpStatus.CREATED)
              .body(groupService.enrollStudent(code, email, principal.getName()));
   }

   @GetMapping("/{code}/stats")
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<List<StudentStatDto>> getGroupStats(@PathVariable String code) {
      return ResponseEntity.ok(groupService.getStudentStats(code));
   }

   @PutMapping("/{id}")
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<GroupDto> update(
           @PathVariable Long id,
           @Valid @RequestBody GroupDto dto,
           Principal principal) {
      return ResponseEntity.ok(groupService.update(id, dto, principal.getName()));
   }

   @DeleteMapping("/{code}/students/{email}")
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<Void> removeStudent(
           @PathVariable String code,
           @PathVariable String email,
           Principal principal) {
      groupService.removeStudentFromGroup(code, email, principal.getName());
      return ResponseEntity.noContent().build();
   }

   @PostMapping("/{code}/join-code")
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<GroupJoinCodeDto> generateJoinCode(
           @PathVariable String code,
           Principal principal) {
      return ResponseEntity.status(HttpStatus.CREATED)
              .body(groupService.generateJoinCode(code, principal.getName()));
   }

   @PostMapping("/join")
   @PreAuthorize("hasAuthority('ESTUDIANTE')")
   public ResponseEntity<StudentSimpleDto> joinGroup(
           @RequestBody Map<String, String> payload,
           Principal principal) {
      String joinCode = payload.get("code");
      return ResponseEntity.status(HttpStatus.CREATED)
              .body(groupService.joinGroupByTemporaryCode(joinCode, principal.getName()));
   }

   @PatchMapping("/{groupId}/topics/{topicId}/assigned")
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<Void> setTopicAssigned(
           @PathVariable Long groupId,
           @PathVariable Long topicId,
           @RequestParam boolean assigned) {
      groupService.setTopicAssigned(groupId, topicId, assigned);
      return ResponseEntity.noContent().build();
   }

   @DeleteMapping("/{id}")
   @PreAuthorize("hasAuthority('DOCENTE')")
   public ResponseEntity<Void> deleteGroup(
           @PathVariable Long id,
           Principal principal) {
      groupService.deleteGroup(id, principal.getName());
      return ResponseEntity.noContent().build();
   }
}