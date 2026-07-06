package com.upc.learntrack.course.service.impl;

import com.upc.learntrack.course.dto.TeacherDto;
import com.upc.learntrack.course.exception.TeacherNotFoundException;
import com.upc.learntrack.course.mapper.TeacherMapper;
import com.upc.learntrack.course.model.Teacher;
import com.upc.learntrack.course.repository.TeacherRepository;
import com.upc.learntrack.course.service.TeacherService;
import com.upc.learntrack.iam.exception.UserNotFoundException;
import com.upc.learntrack.iam.model.User;
import com.upc.learntrack.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final TeacherMapper teacherMapper;

    @Override
    public List<TeacherDto> findAll() {
        return teacherRepository.findAll().stream()
                .map(teacherMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TeacherDto findById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Profesor no encontrado con ID: " + id));
        return teacherMapper.toDto(teacher);
    }

    @Override
    public TeacherDto save(TeacherDto dto) {
        if (teacherRepository.existsByUserId(dto.getUserId())) {
            throw new IllegalArgumentException("El usuario ya tiene un perfil de profesor asignado.");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Usuario (institucional) no encontrado con ID: " + dto.getUserId()));

        Teacher teacher = teacherMapper.toEntity(dto);
        teacher.setUser(user);
        return teacherMapper.toDto(teacherRepository.save(teacher));
    }
}