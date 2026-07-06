package com.upc.learntrack.learningpath.mapper;

import com.upc.learntrack.learningpath.dto.LearningPathDto;
import com.upc.learntrack.learningpath.model.LearningPath;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {PathNodeMapper.class, ConceptualGapMapper.class})
public interface LearningPathMapper {

   @Mapping(source = "learningCollection.name", target = "collectionName")
   LearningPathDto toDto(LearningPath entity);
}