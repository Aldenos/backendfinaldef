package com.upc.learntrack.course.mapper;

import com.upc.learntrack.course.dto.LearningCollectionDto;
import com.upc.learntrack.course.model.LearningCollection;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LearningCollectionMapper {
    LearningCollectionDto toDto(LearningCollection learningCollection);
    LearningCollection toEntity(LearningCollectionDto learningCollectionDto);
}