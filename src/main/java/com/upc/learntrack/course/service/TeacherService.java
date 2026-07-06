package com.upc.learntrack.course.service;

import com.upc.learntrack.course.dto.TeacherDto;
import java.util.List;

public interface TeacherService {
    List<TeacherDto> findAll();
    TeacherDto findById(Long id);
    TeacherDto save(TeacherDto dto);
}