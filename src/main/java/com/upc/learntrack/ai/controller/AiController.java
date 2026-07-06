package com.upc.learntrack.ai.controller;

import com.upc.learntrack.ai.dto.GenerateActivityResponseDto;
import com.upc.learntrack.ai.service.AiGeneratorService;
import com.upc.learntrack.shared.service.PdfTextExtractorService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiGeneratorService aiService;
    private final PdfTextExtractorService pdfTextExtractor;

    @PostMapping("/upload-activity")
    @PreAuthorize("hasAnyAuthority('DOCENTE', 'ESTUDIANTE')")
    public ResponseEntity<GenerateActivityResponseDto> uploadAndGenerate(
            @RequestParam("file") @NotNull MultipartFile file,
            @RequestParam("topicName") @NotEmpty String topicName,
            @RequestParam("types") @NotEmpty List<String> types,
            Principal principal) throws IOException {

        // Validar tipo de archivo
        if (!"application/pdf".equals(file.getContentType())) {
            throw new IllegalArgumentException("Solo se permiten archivos PDF");
        }

        // Validar tamaño máximo (ej. 5 MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("El archivo no puede superar los 5 MB");
        }

        String content = pdfTextExtractor.extractText(file);
        GenerateActivityResponseDto result = aiService.generateActivity(topicName, content, principal.getName(), types);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}