package com.upc.learntrack.course.controller;

import com.upc.learntrack.course.dto.TeacherDto;
import com.upc.learntrack.course.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherController {

   private final TeacherService teacherService;

   @GetMapping
   public ResponseEntity<List<TeacherDto>> findAll() {
       return ResponseEntity.ok(teacherService.findAll());
   }

   @GetMapping("/{id}")
   public ResponseEntity<TeacherDto> findById(@PathVariable Long id) {
       return ResponseEntity.ok(teacherService.findById(id));
   }
}