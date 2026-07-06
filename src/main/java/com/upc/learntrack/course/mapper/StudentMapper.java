package com.upc.learntrack.course.mapper;

import com.upc.learntrack.course.dto.StudentDto;
import com.upc.learntrack.course.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    @Mapping(source = "user.id", target = "userId")
    StudentDto toDto(Student student);

    @Mapping(source = "userId", target = "user.id")
    Student toEntity(StudentDto studentDto);
}