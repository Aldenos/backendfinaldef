package com.upc.learntrack.learningpath.mapper;

import com.upc.learntrack.learningpath.dto.ConceptualGapDto;
import com.upc.learntrack.learningpath.model.ConceptualGap;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConceptualGapMapper {
   @Mapping(source = "topic.name", target = "topicName")
   @Mapping(target = "resolved", source = "resolved")
   ConceptualGapDto toDto(ConceptualGap entity);
}