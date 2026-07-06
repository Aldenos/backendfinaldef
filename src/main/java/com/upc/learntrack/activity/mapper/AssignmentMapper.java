package com.upc.learntrack.activity.mapper;

import com.upc.learntrack.activity.dto.AssignmentDto;
import com.upc.learntrack.activity.model.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssignmentMapper {

   @Mapping(source = "group.code", target = "groupCode")
   @Mapping(source = "activity.title", target = "activityTitle")
   AssignmentDto toDto(Assignment entity);

   @Mapping(target = "group", ignore = true)
   @Mapping(target = "activity", ignore = true)
   Assignment toEntity(AssignmentDto dto);
}