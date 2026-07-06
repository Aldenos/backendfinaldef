package com.upc.learntrack.course.mapper;

import com.upc.learntrack.course.dto.EnrollmentDto;
import com.upc.learntrack.course.model.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    @Mapping(source = "student.user.email", target = "studentEmail")
    @Mapping(source = "group.code", target = "groupCode")
    EnrollmentDto toDto(Enrollment enrollment);

    @Mapping(target = "student", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "enrolledAt", ignore = true) 
    Enrollment toEntity(EnrollmentDto enrollmentDto);
}