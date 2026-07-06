package com.upc.learntrack.activity.mapper;

import com.upc.learntrack.activity.dto.QuestionOptionDto;
import com.upc.learntrack.activity.model.QuestionOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuestionOptionMapper {

   QuestionOptionDto toDto(QuestionOption entity);

   @Mapping(target = "id", ignore = true)
   @Mapping(target = "question", ignore = true)
   QuestionOption toEntity(QuestionOptionDto dto);
}