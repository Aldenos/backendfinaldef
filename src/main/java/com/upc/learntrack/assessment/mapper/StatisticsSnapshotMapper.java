package com.upc.learntrack.assessment.mapper;

import com.upc.learntrack.assessment.dto.StatisticsSnapshotDto;
import com.upc.learntrack.assessment.model.StatisticsSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StatisticsSnapshotMapper {
   
   @Mapping(source = "group.code", target = "groupCode")
   @Mapping(source = "topic.name", target = "topicName")
   StatisticsSnapshotDto toDto(StatisticsSnapshot entity);
   StatisticsSnapshot toEntity(StatisticsSnapshotDto dto);
}