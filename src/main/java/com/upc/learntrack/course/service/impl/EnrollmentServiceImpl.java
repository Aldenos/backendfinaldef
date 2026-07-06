package com.upc.learntrack.course.service.impl;

import com.upc.learntrack.course.dto.EnrollmentDto;
import com.upc.learntrack.course.exception.GroupNotFoundException;
import com.upc.learntrack.course.exception.StudentNotFoundException;
import com.upc.learntrack.course.model.Enrollment;
import com.upc.learntrack.course.model.Group;
import com.upc.learntrack.course.model.Student;
import com.upc.learntrack.course.repository.EnrollmentRepository;
import com.upc.learntrack.course.repository.GroupRepository;
import com.upc.learntrack.course.repository.StudentRepository;
import com.upc.learntrack.course.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public EnrollmentDto enrollStudent(String groupCode, EnrollmentDto dto) {
        // 1. Buscamos el grupo por código
        Group group = groupRepository.findByCode(groupCode)
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado con código: " + groupCode));

        // 2. Buscamos al alumno por email
        Student student = studentRepository.findByUserEmail(dto.getStudentEmail())
                .orElseThrow(() -> new StudentNotFoundException("Estudiante no encontrado con email: " + dto.getStudentEmail()));

        // 3. Registramos la matrícula
        Enrollment enrollment = new Enrollment();
        enrollment.setGroup(group);
        enrollment.setStudent(student);
        
        Enrollment saved = enrollmentRepository.save(enrollment);
        
        // 4. Mapeo manual rápido para evitar conflictos con MapStruct antiguo
        EnrollmentDto response = new EnrollmentDto();
        response.setId(saved.getId());
        response.setGroupCode(group.getCode());
        response.setStudentEmail(dto.getStudentEmail());
        
        return response;
    }
}