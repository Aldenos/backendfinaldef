package com.upc.learntrack.learningpath.service.impl;

import com.upc.learntrack.activity.model.Question;
import com.upc.learntrack.assessment.model.ActivityResult;
import com.upc.learntrack.assessment.model.StudentAnswer;
import com.upc.learntrack.assessment.repository.ActivityResultRepository;
import com.upc.learntrack.course.exception.GroupNotFoundException;
import com.upc.learntrack.course.model.*;
import com.upc.learntrack.course.repository.GroupRepository;
import com.upc.learntrack.learningpath.dto.ConceptualGapDto;
import com.upc.learntrack.learningpath.dto.GroupGapDto;
import com.upc.learntrack.learningpath.dto.SubTopicGapDto;
import com.upc.learntrack.learningpath.exception.ConceptualGapNotFoundException;
import com.upc.learntrack.learningpath.exception.LearningPathNotFoundException;
import com.upc.learntrack.learningpath.mapper.ConceptualGapMapper;
import com.upc.learntrack.learningpath.model.LearningPath;
import com.upc.learntrack.learningpath.repository.ConceptualGapRepository;
import com.upc.learntrack.learningpath.repository.LearningPathRepository;
import com.upc.learntrack.learningpath.service.ConceptualGapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConceptualGapServiceImpl implements ConceptualGapService {

    private final ConceptualGapRepository repository;
    private final ConceptualGapMapper mapper;
    private final GroupRepository groupRepository;
    private final ActivityResultRepository activityResultRepository;
    private final LearningPathRepository learningPathRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ConceptualGapDto> findAllByLearningPathId(Long learningPathId) {
        return repository.findAllByLearningPathId(learningPathId)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void resolveGap(Long id) {
        var gap = repository.findById(id)
                .orElseThrow(() -> new ConceptualGapNotFoundException("Vacío conceptual no encontrado con ID: " + id));
        gap.setResolved(true);
        repository.save(gap);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupGapDto> findGroupGaps(String groupCode) {
        Group group = groupRepository.findByCode(groupCode)
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado con código: " + groupCode));

        List<Student> students = group.getEnrollments().stream()
                .map(Enrollment::getStudent)
                .filter(Objects::nonNull)
                .toList();

        if (students.isEmpty()) return Collections.emptyList();

        Set<Long> studentIds = students.stream().map(Student::getId).collect(Collectors.toSet());

        List<ActivityResult> allResults = activityResultRepository.findAll().stream()
                .filter(r -> studentIds.contains(r.getStudent().getId()))
                .toList();

        Map<Topic, List<ActivityResult>> resultsByTopic = allResults.stream()
                .collect(Collectors.groupingBy(r -> r.getActivity().getTopic()));

        List<GroupGapDto> gaps = new ArrayList<>();
        BigDecimal threshold = new BigDecimal("70.00");

        for (Map.Entry<Topic, List<ActivityResult>> entry : resultsByTopic.entrySet()) {
            Topic topic = entry.getKey();
            List<ActivityResult> topicResults = entry.getValue();

            double avg = topicResults.stream()
                    .mapToDouble(r -> r.getScore().doubleValue())
                    .average().orElse(0.0);

            long belowCount = topicResults.stream()
                    .filter(r -> r.getScore().compareTo(threshold) < 0)
                    .count();

            if (avg < 70.0) {
                GroupGapDto dto = new GroupGapDto();
                dto.setTopicName(topic.getName());
                dto.setAverageMastery(BigDecimal.valueOf(avg));
                dto.setStudentsBelowThreshold((int) belowCount);
                dto.setRecommendation(avg < 50.0 ? "Reenseñar" : "Reforzar");
                gaps.add(dto);
            }
        }

        gaps.sort(Comparator.comparing(GroupGapDto::getAverageMastery));
        return gaps;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubTopicGapDto> findSubTopicGapsByGroup(String groupCode) {
        Group group = groupRepository.findByCode(groupCode)
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado con código: " + groupCode));
        List<Student> students = group.getEnrollments().stream()
                .map(Enrollment::getStudent)
                .filter(Objects::nonNull)
                .toList();
        if (students.isEmpty()) return Collections.emptyList();

        Set<Long> studentIds = students.stream().map(Student::getId).collect(Collectors.toSet());

        List<ActivityResult> allResults = activityResultRepository.findAll().stream()
                .filter(r -> studentIds.contains(r.getStudent().getId()))
                .toList();

        Map<SubTopic, Integer> totalBySubTopic = new HashMap<>();
        Map<SubTopic, Integer> incorrectBySubTopic = new HashMap<>();

        for (ActivityResult result : allResults) {
            for (StudentAnswer answer : result.getAnswers()) {
                Question q = answer.getQuestion();
                SubTopic st = q.getSubTopic();
                if (st == null) continue;
                totalBySubTopic.merge(st, 1, Integer::sum);
                if (!answer.getIsCorrect()) {
                    incorrectBySubTopic.merge(st, 1, Integer::sum);
                }
            }
        }

        List<SubTopicGapDto> gaps = new ArrayList<>();
        for (SubTopic st : totalBySubTopic.keySet()) {
            int total = totalBySubTopic.get(st);
            int incorrect = incorrectBySubTopic.getOrDefault(st, 0);
            double errorRate = total == 0 ? 0 : (incorrect * 100.0 / total);
            if (errorRate > 30) {
                SubTopicGapDto dto = new SubTopicGapDto();
                dto.setSubTopicName(st.getName());
                dto.setTopicName(st.getTopic().getName());
                dto.setErrorRate(BigDecimal.valueOf(errorRate));
                dto.setTotalQuestions(total);
                dto.setIncorrectAnswers(incorrect);
                gaps.add(dto);
            }
        }
        gaps.sort(Comparator.comparing(SubTopicGapDto::getErrorRate).reversed());
        return gaps;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubTopicGapDto> findSubTopicGapsByLearningPath(Long learningPathId) {
        LearningPath learningPath = learningPathRepository.findById(learningPathId)
                .orElseThrow(() -> new LearningPathNotFoundException("Ruta de aprendizaje no encontrada con ID: " + learningPathId));
        Long studentId = learningPath.getStudent().getId();

        List<ActivityResult> allResults = activityResultRepository.findAllByStudentId(studentId);

        Map<SubTopic, int[]> subTopicStats = new HashMap<>();
        for (ActivityResult result : allResults) {
            for (StudentAnswer answer : result.getAnswers()) {
                Question q = answer.getQuestion();
                SubTopic st = q.getSubTopic();
                if (st == null) continue;
                int[] stats = subTopicStats.getOrDefault(st, new int[]{0, 0});
                stats[0]++; // total
                if (!answer.getIsCorrect()) stats[1]++; // incorrect
                subTopicStats.put(st, stats);
            }
        }

        List<SubTopicGapDto> gaps = new ArrayList<>();
        for (Map.Entry<SubTopic, int[]> entry : subTopicStats.entrySet()) {
            SubTopic st = entry.getKey();
            int total = entry.getValue()[0];
            int incorrect = entry.getValue()[1];
            double errorRate = total == 0 ? 0 : (incorrect * 100.0 / total);
            if (errorRate > 30) {
                SubTopicGapDto dto = new SubTopicGapDto();
                dto.setSubTopicName(st.getName());
                dto.setTopicName(st.getTopic().getName());
                dto.setErrorRate(BigDecimal.valueOf(errorRate));
                dto.setTotalQuestions(total);
                dto.setIncorrectAnswers(incorrect);
                gaps.add(dto);
            }
        }
        gaps.sort(Comparator.comparing(SubTopicGapDto::getErrorRate).reversed());
        return gaps;
    }
}