package com.upc.learntrack.course.controller;

import com.upc.learntrack.course.dto.EnrollmentDto;
import com.upc.learntrack.course.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/group/{groupCode}")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<EnrollmentDto> enrollStudent(
            @PathVariable String groupCode,
            @Valid @RequestBody EnrollmentDto dto) {
        return new ResponseEntity<>(enrollmentService.enrollStudent(groupCode, dto), HttpStatus.CREATED);
    }
}