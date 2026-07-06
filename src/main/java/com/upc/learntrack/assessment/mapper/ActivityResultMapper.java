package com.upc.learntrack.assessment.mapper;

import com.upc.learntrack.assessment.dto.ActivityResultDto;
import com.upc.learntrack.assessment.model.ActivityResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActivityResultMapper {
   @Mapping(source = "activity.title", target = "activityTitle")
   @Mapping(source = "student.user.email", target = "studentEmail")
   ActivityResultDto toDto(ActivityResult entity);
}