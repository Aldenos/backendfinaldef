package com.upc.learntrack.assessment.repository;

import com.upc.learntrack.assessment.model.PdfReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PdfReportRepository extends JpaRepository<PdfReport, Long> {
    List<PdfReport> findAllByGroup_Code(String groupCode);
}