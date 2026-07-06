package com.upc.learntrack.course.mapper;

import com.upc.learntrack.course.dto.CollectionGroupDto;
import com.upc.learntrack.course.model.CollectionGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CollectionGroupMapper {
    
    @Mapping(source = "learningCollection.id", target = "learningCollectionId")
    @Mapping(source = "group.id", target = "groupId")
    CollectionGroupDto toDto(CollectionGroup collectionGroup);

    @Mapping(source = "learningCollectionId", target = "learningCollection.id")
    @Mapping(source = "groupId", target = "group.id")
    CollectionGroup toEntity(CollectionGroupDto collectionGroupDto);
}