package com.upc.learntrack.course.mapper;

import com.upc.learntrack.course.dto.TeacherDto;
import com.upc.learntrack.course.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeacherMapper {
    
    @Mapping(source = "user.id", target = "userId")
    TeacherDto toDto(Teacher teacher);

    @Mapping(source = "userId", target = "user.id")
    Teacher toEntity(TeacherDto teacherDto);
}