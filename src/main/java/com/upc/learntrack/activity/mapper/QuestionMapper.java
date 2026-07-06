package com.upc.learntrack.activity.mapper;

import com.upc.learntrack.activity.dto.QuestionDto;
import com.upc.learntrack.activity.model.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {QuestionOptionMapper.class})
public interface QuestionMapper {

   QuestionDto toDto(Question entity);

   @Mapping(target = "id", ignore = true)
   @Mapping(target = "activity", ignore = true)
   @Mapping(target = "subTopic", ignore = true)
   Question toEntity(QuestionDto dto);
}