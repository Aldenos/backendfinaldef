package com.upc.learntrack.assessment.service.impl;

import com.upc.learntrack.assessment.dto.PdfReportDto;
import com.upc.learntrack.assessment.exception.PdfReportNotFoundException;
import com.upc.learntrack.assessment.mapper.PdfReportMapper;
import com.upc.learntrack.assessment.model.PdfReport;
import com.upc.learntrack.assessment.repository.PdfReportRepository;
import com.upc.learntrack.assessment.service.PdfReportService;
import com.upc.learntrack.course.exception.GroupNotFoundException;
import com.upc.learntrack.course.exception.LearningCollectionNotFoundException;
import com.upc.learntrack.course.model.Group;
import com.upc.learntrack.course.model.LearningCollection;
import com.upc.learntrack.course.repository.GroupRepository;
import com.upc.learntrack.course.repository.LearningCollectionRepository;
import com.upc.learntrack.shared.service.EmailService;
import com.upc.learntrack.shared.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PdfReportServiceImpl implements PdfReportService {

    private final PdfReportRepository pdfReportRepository;
    private final GroupRepository groupRepository;
    private final LearningCollectionRepository learningCollectionRepository;
    private final PdfReportMapper mapper;
    private final EmailService emailService;
    private final PdfGeneratorService pdfGeneratorService;

    @Override
    @Transactional(readOnly = true)
    public List<PdfReportDto> findAllByGroupCode(String groupCode) {
        return pdfReportRepository.findAllByGroup_Code(groupCode).stream()
                .map(mapper::toDto).toList();
    }

    @Override
    @Transactional
    public PdfReportDto save(PdfReportDto dto, String teacherEmail) {
        Group group = groupRepository.findByCode(dto.getGroupCode())
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado: " + dto.getGroupCode()));
        LearningCollection collection = learningCollectionRepository.findByName(dto.getCollectionName())
                .orElseThrow(() -> new LearningCollectionNotFoundException("Colección no encontrada: " + dto.getCollectionName()));

        // 1. Generar PDF
        Map<String, Object> data = new HashMap<>();
        data.put("groupName", group.getName());
        data.put("collectionName", collection.getName());
        data.put("date", LocalDateTime.now().toString());
        byte[] pdfBytes = pdfGeneratorService.generatePdf("report-template", data);

        // 2. Crear entidad con el PDF embebido
        PdfReport report = mapper.toEntity(dto);
        report.setGroup(group);
        report.setLearningCollection(collection);
        report.setSentTo(teacherEmail);
        report.setPdfContent(pdfBytes);
        report.setFileUrl("generated_" + System.currentTimeMillis());

        PdfReport savedReport = pdfReportRepository.save(report);

        // 3. Enviar correo (asíncrono)
        List<String> recipients = new ArrayList<>();
        recipients.add(teacherEmail);
        recipients.addAll(group.getStudentEmails());
        if (dto.getOptionalEmail() != null && !dto.getOptionalEmail().isBlank()) {
            recipients.add(dto.getOptionalEmail());
        }
        emailService.sendReport(recipients, "Reporte académico: " + collection.getName(),
                "Estimados, se adjunta el reporte de progreso generado.", pdfBytes,
                "Reporte_" + group.getCode() + ".pdf");

        return mapper.toDto(savedReport);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadReportPdf(Long reportId) {
        PdfReport report = pdfReportRepository.findById(reportId)
                .orElseThrow(() -> new PdfReportNotFoundException("Reporte no encontrado con id: " + reportId));

        if (report.getPdfContent() != null) {
            return report.getPdfContent();
        }

        // Fallback: regenerar
        Map<String, Object> data = new HashMap<>();
        data.put("groupName", report.getGroup().getName());
        data.put("collectionName", report.getLearningCollection().getName());
        data.put("date", report.getCreatedAt().toString());
        return pdfGeneratorService.generatePdf("report-template", data);
    }
}