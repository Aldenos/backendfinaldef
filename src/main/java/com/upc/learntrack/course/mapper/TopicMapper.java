package com.upc.learntrack.course.mapper;

import com.upc.learntrack.course.dto.TopicDto;
import com.upc.learntrack.course.model.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TopicMapper {

    TopicDto toDto(Topic topic);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "learningCollection", ignore = true)
    @Mapping(target = "orderIdx", ignore = true)
    Topic toEntity(TopicDto dto);
}