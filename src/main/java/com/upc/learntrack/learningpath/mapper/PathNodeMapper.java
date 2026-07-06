package com.upc.learntrack.learningpath.mapper;

import com.upc.learntrack.learningpath.dto.PathNodeDto;
import com.upc.learntrack.learningpath.model.PathNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PathNodeMapper {

   @Mapping(source = "topic.name", target = "topicName")
   PathNodeDto toDto(PathNode entity);
}