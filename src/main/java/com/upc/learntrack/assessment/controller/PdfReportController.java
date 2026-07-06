package com.upc.learntrack.assessment.controller;

import com.upc.learntrack.assessment.dto.PdfReportDto;
import com.upc.learntrack.assessment.service.PdfReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PdfReportController {

    private final PdfReportService service;

    @GetMapping("/groups/{groupCode}/reports")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<List<PdfReportDto>> findAllByGroup(@PathVariable String groupCode) {
        return ResponseEntity.ok(service.findAllByGroupCode(groupCode));
    }

    @PostMapping("/reports")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<PdfReportDto> save(@Valid @RequestBody PdfReportDto dto, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.save(dto, principal.getName()));
    }

    @GetMapping("/reports/{id}/download")
    @PreAuthorize("hasAuthority('DOCENTE')")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long id) {
        byte[] pdfData = service.downloadReportPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report_" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }
}