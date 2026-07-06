package com.upc.learntrack.assessment.mapper;

import com.upc.learntrack.assessment.dto.PdfReportDto;
import com.upc.learntrack.assessment.model.PdfReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PdfReportMapper {
   
   @Mapping(source = "group.code", target = "groupCode")
   @Mapping(source = "learningCollection.name", target = "collectionName")
   PdfReportDto toDto(PdfReport entity);
   PdfReport toEntity(PdfReportDto dto);
}