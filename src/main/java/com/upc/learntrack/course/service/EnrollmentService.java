package com.upc.learntrack.course.service;

import com.upc.learntrack.course.dto.EnrollmentDto;

public interface EnrollmentService {
    EnrollmentDto enrollStudent(String groupCode, EnrollmentDto dto);
}