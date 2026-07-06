package com.upc.learntrack.course.controller;

import com.upc.learntrack.course.dto.StudentDto;
import com.upc.learntrack.course.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

   private final StudentService studentService;

   @GetMapping
   public ResponseEntity<List<StudentDto>> findAll() {
       return ResponseEntity.ok(studentService.findAll());
   }

   @GetMapping("/{id}")
   public ResponseEntity<StudentDto> findById(@PathVariable Long id) {
       return ResponseEntity.ok(studentService.findById(id));
   }
}