package com.upc.learntrack.course.service.impl;

import com.upc.learntrack.course.dto.TopicDto;
import com.upc.learntrack.course.exception.LearningCollectionNotFoundException;
import com.upc.learntrack.course.exception.StudentNotFoundException;
import com.upc.learntrack.course.exception.TeacherNotFoundException;
import com.upc.learntrack.course.exception.TopicNotFoundException;
import com.upc.learntrack.course.mapper.TopicMapper;
import com.upc.learntrack.course.model.*;
import com.upc.learntrack.course.repository.*;
import com.upc.learntrack.course.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.upc.learntrack.course.exception.GroupNotFoundException;
import com.upc.learntrack.course.repository.GroupRepository;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final LearningCollectionRepository learningCollectionRepository;
    private final TopicMapper topicMapper;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GroupTopicPriorityRepository groupTopicPriorityRepository;
    private final TeacherRepository teacherRepository;
    private final GroupRepository groupRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TopicDto> findAllByCollectionName(String collectionName, String teacherEmail) {
        LearningCollection collection;
        if (teacherEmail != null) {
            Teacher teacher = teacherRepository.findByUserEmail(teacherEmail).orElse(null);
            if (teacher != null) {
                collection = learningCollectionRepository.findByNameAndTeacherId(collectionName, teacher.getId())
                        .orElseThrow(() -> new LearningCollectionNotFoundException("Colección no encontrada: " + collectionName));
            } else {
                collection = learningCollectionRepository.findByName(collectionName)
                        .orElseThrow(() -> new LearningCollectionNotFoundException("Colección no encontrada: " + collectionName));
            }
        } else {
            collection = learningCollectionRepository.findByName(collectionName)
                    .orElseThrow(() -> new LearningCollectionNotFoundException("Colección no encontrada: " + collectionName));
        }
        return topicRepository.findAllByLearningCollectionIdOrderByOrderIdxAsc(collection.getId()).stream()
                .map(topicMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TopicDto findById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new TopicNotFoundException("Tema no encontrado con ID: " + id));
        return topicMapper.toDto(topic);
    }

    @Override
    @Transactional
    public TopicDto save(String collectionName, TopicDto dto, String teacherEmail) {
        Teacher teacher = teacherRepository.findByUserEmail(teacherEmail)
                .orElseThrow(() -> new TeacherNotFoundException("Docente no encontrado: " + teacherEmail));
        LearningCollection collection = learningCollectionRepository
                .findByNameAndTeacherId(collectionName, teacher.getId())
                .orElseThrow(() -> new LearningCollectionNotFoundException("Colección no encontrada: " + collectionName));
        if (topicRepository.existsByNameAndLearningCollectionId(dto.getName(), collection.getId())) {
            throw new IllegalArgumentException("Ya existe un tema con el nombre '" + dto.getName() + "' en esta colección.");
        }
        Topic topic = topicMapper.toEntity(dto);
        topic.setLearningCollection(collection);
        int existingTopics = topicRepository.findAllByLearningCollectionId(collection.getId()).size();
        topic.setOrderIdx(existingTopics);
        return topicMapper.toDto(topicRepository.save(topic));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicDto> findAll() {
        return topicRepository.findAll().stream()
                .map(topicMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicDto> findPrioritizedTopicsForStudent(String studentEmail) {
        Student student = studentRepository.findByUserEmail(studentEmail)
                .orElseThrow(() -> new StudentNotFoundException("Estudiante no encontrado con email: " + studentEmail));
        List<Long> groupIds = enrollmentRepository.findAllByStudentId(student.getId()).stream()
                .map(e -> e.getGroup().getId())
                .collect(Collectors.toList());
        if (groupIds.isEmpty()) return Collections.emptyList();
        List<GroupTopicPriority> priorities = groupTopicPriorityRepository.findAllByGroupIdInAndPriorityTrue(groupIds);
        Set<Long> topicIds = priorities.stream()
                .map(p -> p.getId().getTopicId())
                .collect(Collectors.toSet());
        if (topicIds.isEmpty()) return Collections.emptyList();
        List<Topic> topics = topicRepository.findAllById(topicIds);
        return topics.stream()
                .map(topicMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TopicDto update(Long id, TopicDto dto, String teacherEmail) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new TopicNotFoundException("Tema no encontrado con ID: " + id));
        Teacher teacher = teacherRepository.findByUserEmail(teacherEmail)
                .orElseThrow(() -> new TeacherNotFoundException("Docente no encontrado"));
        if (!topic.getLearningCollection().getTeacher().getId().equals(teacher.getId())) {
            throw new SecurityException("No tienes permiso para modificar este tema");
        }
        // Verificar nombre duplicado (excepto sí mismo)
        if (!topic.getName().equals(dto.getName()) &&
                topicRepository.existsByNameAndLearningCollectionId(dto.getName(), topic.getLearningCollection().getId())) {
            throw new IllegalArgumentException("Ya existe un tema con el nombre '" + dto.getName() + "' en esta colección.");
        }
        topic.setName(dto.getName());
        // No se modifica orderIdx normalmente, pero se podría permitir
        if (dto.getOrderIdx() != null) {
            topic.setOrderIdx(dto.getOrderIdx());
        }
        return topicMapper.toDto(topicRepository.save(topic));
    }

    @Override
    @Transactional
    public void delete(Long id, String teacherEmail) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new TopicNotFoundException("Tema no encontrado con ID: " + id));
        Teacher teacher = teacherRepository.findByUserEmail(teacherEmail)
                .orElseThrow(() -> new TeacherNotFoundException("Docente no encontrado"));
        if (!topic.getLearningCollection().getTeacher().getId().equals(teacher.getId())) {
            throw new SecurityException("No tienes permiso para eliminar este tema");
        }
        topicRepository.delete(topic);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicDto> findVisibleTopicsForGroup(String collectionName, Long groupId, String userEmail) {
        LearningCollection collection = learningCollectionRepository.findByName(collectionName)
                .orElseThrow(() -> new LearningCollectionNotFoundException("Colección no encontrada: " + collectionName));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado con ID: " + groupId));
        if (!group.getLearningCollection().getId().equals(collection.getId())) {
            throw new IllegalArgumentException("El grupo no pertenece a esta colección.");
        }
        return topicRepository.findVisibleTopicsForGroup(collection.getId(), groupId).stream()
                .map(topicMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignTopicToGroups(Long topicId, List<Long> groupIds, String teacherEmail) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException("Tema no encontrado con ID: " + topicId));
        Teacher teacher = teacherRepository.findByUserEmail(teacherEmail)
                .orElseThrow(() -> new TeacherNotFoundException("Docente no encontrado"));
        if (!topic.getLearningCollection().getTeacher().getId().equals(teacher.getId())) {
            throw new SecurityException("No tienes permiso para modificar este tema");
        }
        // Eliminar restricciones previas
        List<GroupTopicPriority> existing = groupTopicPriorityRepository.findAllByTopicId(topicId);
        groupTopicPriorityRepository.deleteAll(existing);

        // Si groupIds está vacío → sin restricciones (aplica a todos)
        for (Long groupId : groupIds) {
            GroupTopicId id = new GroupTopicId(groupId, topicId);
            GroupTopicPriority gtp = new GroupTopicPriority();
            gtp.setId(id);
            gtp.setAssigned(true);
            gtp.setPriority(false);
            groupTopicPriorityRepository.save(gtp);
        }
    }
}