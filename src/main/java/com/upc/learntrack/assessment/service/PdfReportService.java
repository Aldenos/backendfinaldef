package com.upc.learntrack.assessment.service;

import com.upc.learntrack.assessment.dto.PdfReportDto;
import java.util.List;

public interface PdfReportService {
    List<PdfReportDto> findAllByGroupCode(String groupCode);
    PdfReportDto save(PdfReportDto dto, String teacherEmail);
    byte[] downloadReportPdf(Long reportId);
}