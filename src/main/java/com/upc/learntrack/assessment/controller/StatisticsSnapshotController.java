package com.upc.learntrack.assessment.controller;

import com.upc.learntrack.assessment.dto.StatisticsSnapshotDto;
import com.upc.learntrack.assessment.service.StatisticsSnapshotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StatisticsSnapshotController {

    private final StatisticsSnapshotService service;

    @GetMapping("/groups/{groupCode}/statistics")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<List<StatisticsSnapshotDto>> findAllByGroup(@PathVariable String groupCode) {
        return ResponseEntity.ok(service.findAllByGroupCode(groupCode));
    }

    @PostMapping("/statistics")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<StatisticsSnapshotDto> save(@Valid @RequestBody StatisticsSnapshotDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }
}