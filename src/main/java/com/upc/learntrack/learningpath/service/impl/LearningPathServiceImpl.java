package com.upc.learntrack.learningpath.service.impl;

import com.upc.learntrack.assessment.model.ActivityResult;
import com.upc.learntrack.assessment.repository.ActivityResultRepository;
import com.upc.learntrack.course.exception.GroupNotFoundException;
import com.upc.learntrack.course.exception.LearningCollectionNotFoundException;
import com.upc.learntrack.course.exception.StudentNotFoundException;
import com.upc.learntrack.course.exception.TopicNotFoundException;
import com.upc.learntrack.course.model.*;
import com.upc.learntrack.course.repository.*;
import com.upc.learntrack.learningpath.dto.LearningPathDto;
import com.upc.learntrack.learningpath.dto.StudentLearningPathDto;
import com.upc.learntrack.learningpath.exception.InsufficientDataException;
import com.upc.learntrack.learningpath.exception.LearningPathNotFoundException;
import com.upc.learntrack.learningpath.mapper.LearningPathMapper;
import com.upc.learntrack.learningpath.model.LearningPath;
import com.upc.learntrack.learningpath.model.PathNode;
import com.upc.learntrack.learningpath.repository.LearningPathRepository;
import com.upc.learntrack.learningpath.repository.PathNodeRepository;
import com.upc.learntrack.learningpath.service.LearningPathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearningPathServiceImpl implements LearningPathService {

    private final LearningPathRepository learningPathRepository;
    private final StudentRepository studentRepository;
    private final LearningCollectionRepository learningCollectionRepository;
    private final TopicRepository topicRepository;
    private final ActivityResultRepository activityResultRepository;
    private final GroupRepository groupRepository;
    private final PathNodeRepository pathNodeRepository;
    private final LearningPathMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<LearningPathDto> findAllByStudentEmail(String studentEmail) {
        Student student = studentRepository.findByUserEmail(studentEmail)
                .orElseThrow(() -> new StudentNotFoundException("Estudiante no encontrado con email: " + studentEmail));
        return learningPathRepository.findAllByStudentId(student.getId()).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningPathDto> findAllByStudentId(Long studentId) {
        return learningPathRepository.findAllByStudentId(studentId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LearningPathDto findById(Long id) {
        return learningPathRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new LearningPathNotFoundException("Ruta no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public LearningPathDto getAdaptivePath(String collectionName, String studentEmail) {
        Student student = studentRepository.findByUserEmail(studentEmail)
                .orElseThrow(() -> new StudentNotFoundException("Estudiante no encontrado con email: " + studentEmail));

        LearningCollection collection = learningCollectionRepository.findByName(collectionName)
                .orElseThrow(() -> new LearningCollectionNotFoundException("Colección no encontrada: " + collectionName));

        // Verificar si el estudiante tiene al menos una actividad completada en esta colección
        boolean hasActivityResults = activityResultRepository.findAllByStudentId(student.getId()).stream()
                .anyMatch(ar -> ar.getActivity().getTopic().getLearningCollection().getId().equals(collection.getId()));
        if (!hasActivityResults) {
            throw new InsufficientDataException("Resuelve al menos una actividad para calcular tu ruta hacia el 70% de aprobación.");
        }

        LearningPath path = learningPathRepository
                .findByStudentIdAndLearningCollectionId(student.getId(), collection.getId())
                .orElseGet(() -> createNewLearningPath(student, collection));

        updateCurrentPercentage(path);
        Map<Topic, BigDecimal> masteryByTopic = calculateMasteryByTopic(student, collection);
        reorderPathNodesByMastery(path, masteryByTopic);
        LearningPath updatedPath = learningPathRepository.save(path);
        return mapper.toDto(updatedPath);
    }

    @Override
    @Transactional
    public LearningPathDto getAdaptivePathForTopic(Long topicId, String studentEmail) {
        Student student = studentRepository.findByUserEmail(studentEmail)
                .orElseThrow(() -> new StudentNotFoundException("Estudiante no encontrado con email: " + studentEmail));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException("Tema no encontrado con ID: " + topicId));
        LearningCollection collection = topic.getLearningCollection();

        LearningPath path = learningPathRepository
                .findByStudentIdAndLearningCollectionId(student.getId(), collection.getId())
                .orElseGet(() -> createNewLearningPath(student, collection));

        Map<Topic, BigDecimal> masteryByTopic = calculateMasteryByTopic(student, collection);
        reorderPathNodesByMastery(path, masteryByTopic);
        updateCurrentPercentage(path);
        path = learningPathRepository.save(path);
        return mapper.toDto(path);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentLearningPathDto> findLearningPathsByGroupCode(String groupCode) {
        Group group = groupRepository.findByCode(groupCode)
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado con código: " + groupCode));

        List<Student> students = group.getEnrollments().stream()
                .map(Enrollment::getStudent)
                .filter(Objects::nonNull)
                .toList();

        List<StudentLearningPathDto> result = new ArrayList<>();
        int totalStudents = students.size();
        long completedCount = 0;

        for (Student student : students) {
            List<LearningPath> paths = learningPathRepository.findAllByStudentId(student.getId());
            for (LearningPath path : paths) {
                StudentLearningPathDto dto = new StudentLearningPathDto();
                dto.setStudentName(student.getFirstName() + " " + student.getLastName());
                dto.setStudentEmail(student.getUser().getEmail());
                dto.setCollectionName(path.getLearningCollection().getName());
                dto.setCurrentPercentage(path.getCurrentPercentage());
                dto.setStatus(path.getStatus());
                long completed = path.getPathNodes().stream().filter(PathNode::getCompleted).count();
                dto.setCompletedTopics((int) completed);
                dto.setTotalTopics(path.getPathNodes().size());
                if (path.getCurrentPercentage().compareTo(path.getTargetPercentage()) >= 0) {
                    completedCount++;
                }
                result.add(dto);
            }
        }
        final int rate = totalStudents == 0 ? 0 : (int) ((double) completedCount / totalStudents * 100);
        result.forEach(dto -> dto.setCompletionRate(rate));
        return result;
    }

    // Métodos privados auxiliares

    private LearningPath createNewLearningPath(Student student, LearningCollection collection) {
        LearningPath newPath = new LearningPath();
        newPath.setStudent(student);
        newPath.setLearningCollection(collection);
        newPath.setTargetPercentage(new BigDecimal("70.00"));
        newPath.setStatus("IN_PROGRESS");
        newPath.setCurrentPercentage(BigDecimal.ZERO);

        List<Topic> topics = topicRepository.findAllByLearningCollectionIdOrderByOrderIdxAsc(collection.getId());
        if (topics != null && !topics.isEmpty()) {
            List<PathNode> nodes = new ArrayList<>();
            for (int i = 0; i < topics.size(); i++) {
                Topic topic = topics.get(i);
                PathNode node = new PathNode();
                node.setLearningPath(newPath);
                node.setTopic(topic);
                node.setOrderIdx(i);
                node.setCompleted(false);
                node.setMasteryScore(BigDecimal.ZERO);
                nodes.add(node);
            }
            newPath.setPathNodes(nodes);
        }
        return learningPathRepository.save(newPath);
    }

    private void updateCurrentPercentage(LearningPath path) {
        List<PathNode> nodes = path.getPathNodes();
        if (nodes == null || nodes.isEmpty()) {
            path.setCurrentPercentage(BigDecimal.ZERO);
            return;
        }
        double sum = nodes.stream()
                .mapToDouble(node -> node.getMasteryScore().doubleValue())
                .sum();
        double avg = sum / nodes.size();
        path.setCurrentPercentage(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
    }

    private Map<Topic, BigDecimal> calculateMasteryByTopic(Student student, LearningCollection collection) {
        List<ActivityResult> results = activityResultRepository.findAll().stream()
                .filter(r -> r.getStudent().getId().equals(student.getId()))
                .filter(r -> r.getActivity().getTopic().getLearningCollection().getId().equals(collection.getId()))
                .toList();

        Map<Topic, List<ActivityResult>> grouped = results.stream()
                .collect(Collectors.groupingBy(r -> r.getActivity().getTopic()));

        Map<Topic, BigDecimal> mastery = new HashMap<>();
        for (Map.Entry<Topic, List<ActivityResult>> entry : grouped.entrySet()) {
            double avg = entry.getValue().stream()
                    .mapToDouble(r -> r.getScore().doubleValue())
                    .average()
                    .orElse(0.0);
            mastery.put(entry.getKey(), BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
        }

        List<Topic> allTopics = topicRepository.findAllByLearningCollectionIdOrderByOrderIdxAsc(collection.getId());
        for (Topic topic : allTopics) {
            mastery.putIfAbsent(topic, BigDecimal.ZERO);
        }
        return mastery;
    }

    private void reorderPathNodesByMastery(LearningPath path, Map<Topic, BigDecimal> masteryByTopic) {
        List<PathNode> nodes = path.getPathNodes();
        if (nodes == null || nodes.isEmpty()) return;

        for (PathNode node : nodes) {
            BigDecimal mastery = masteryByTopic.getOrDefault(node.getTopic(), BigDecimal.ZERO);
            node.setMasteryScore(mastery);
        }

        nodes.sort(Comparator.comparing(PathNode::getMasteryScore));

        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).setOrderIdx(i);
        }

        path.setPathNodes(nodes);
        pathNodeRepository.saveAll(nodes);
    }
}