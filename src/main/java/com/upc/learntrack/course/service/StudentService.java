package com.upc.learntrack.course.service;

import com.upc.learntrack.course.dto.StudentDto;
import java.util.List;

public interface StudentService {
    List<StudentDto> findAll();
    StudentDto findById(Long id);
    StudentDto save(StudentDto dto);
}