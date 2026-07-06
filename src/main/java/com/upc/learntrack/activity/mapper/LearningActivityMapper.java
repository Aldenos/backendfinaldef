package com.upc.learntrack.activity.mapper;

import com.upc.learntrack.activity.dto.LearningActivityDto;
import com.upc.learntrack.activity.model.LearningActivity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {QuestionMapper.class})
public interface LearningActivityMapper {

   @Mapping(target = "topicId", source = "topic.id")
   @Mapping(target = "topicName", source = "topic.name")
   LearningActivityDto toDto(LearningActivity entity);

   @Mapping(target = "createdAt", ignore = true)
   @Mapping(target = "generatedByAi", ignore = true)
   @Mapping(target = "personal", ignore = true)
   @Mapping(target = "assignments", ignore = true)
   @Mapping(target = "topic", ignore = true)
   LearningActivity toEntity(LearningActivityDto dto);
}