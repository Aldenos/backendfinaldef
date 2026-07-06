package com.upc.learntrack.course.service.impl;

import com.upc.learntrack.course.dto.StudentDto;
import com.upc.learntrack.course.exception.StudentNotFoundException;
import com.upc.learntrack.course.mapper.StudentMapper;
import com.upc.learntrack.course.model.Student;
import com.upc.learntrack.course.repository.StudentRepository;
import com.upc.learntrack.course.service.StudentService;
import com.upc.learntrack.iam.exception.UserNotFoundException;
import com.upc.learntrack.iam.model.User;
import com.upc.learntrack.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final StudentMapper studentMapper;

    @Override
    public List<StudentDto> findAll() {
        return studentRepository.findAll().stream()
                .map(studentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDto findById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Estudiante no encontrado con ID: " + id));
        return studentMapper.toDto(student);
    }

    @Override
    public StudentDto save(StudentDto dto) {
        if (studentRepository.existsByUserId(dto.getUserId())) {
            throw new IllegalArgumentException("El usuario ya tiene un perfil de estudiante asignado.");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Usuario (institucional) no encontrado con ID: " + dto.getUserId()));

        Student student = studentMapper.toEntity(dto);
        student.setUser(user);
        return studentMapper.toDto(studentRepository.save(student));
    }
}