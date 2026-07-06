package com.upc.learntrack.assessment.service.impl;

import com.upc.learntrack.assessment.dto.StatisticsSnapshotDto;
import com.upc.learntrack.assessment.mapper.StatisticsSnapshotMapper;
import com.upc.learntrack.assessment.model.ActivityResult;
import com.upc.learntrack.assessment.model.StatisticsSnapshot;
import com.upc.learntrack.assessment.repository.ActivityResultRepository;
import com.upc.learntrack.assessment.repository.StatisticsSnapshotRepository;
import com.upc.learntrack.assessment.service.StatisticsSnapshotService;
import com.upc.learntrack.course.exception.GroupNotFoundException;
import com.upc.learntrack.course.exception.TopicNotFoundException;
import com.upc.learntrack.course.model.Enrollment;
import com.upc.learntrack.course.model.Group;
import com.upc.learntrack.course.model.Student;
import com.upc.learntrack.course.model.Topic;
import com.upc.learntrack.course.repository.GroupRepository;
import com.upc.learntrack.course.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsSnapshotServiceImpl implements StatisticsSnapshotService {

    private final StatisticsSnapshotRepository snapshotRepository;
    private final ActivityResultRepository activityResultRepository;
    private final GroupRepository groupRepository;
    private final TopicRepository topicRepository;
    private final StatisticsSnapshotMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<StatisticsSnapshotDto> findAllByGroupCode(String groupCode) {
        return snapshotRepository.findAllByGroup_Code(groupCode).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public StatisticsSnapshotDto save(StatisticsSnapshotDto dto) {
        Group group = groupRepository.findByCode(dto.getGroupCode())
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado con código: " + dto.getGroupCode()));

        Topic topic = topicRepository.findByName(dto.getTopicName())
                .orElseThrow(() -> new TopicNotFoundException("Tema no encontrado: " + dto.getTopicName()));

        List<Long> studentIds = group.getEnrollments().stream()
                .map(Enrollment::getStudent)
                .filter(Objects::nonNull)
                .map(Student::getId)
                .collect(Collectors.toList());

        if (studentIds.isEmpty()) {
            log.warn("El grupo {} no tiene estudiantes inscritos. No se puede calcular estadística.", group.getCode());
            throw new IllegalStateException("El grupo no tiene estudiantes inscritos.");
        }

        List<ActivityResult> results = activityResultRepository.findAllByStudentIdsAndTopicId(studentIds, topic.getId());

        double averageScore = results.stream()
                .mapToDouble(r -> r.getScore().doubleValue())
                .average()
                .orElse(0.0);

        StatisticsSnapshot snapshot = new StatisticsSnapshot();
        snapshot.setGroup(group);
        snapshot.setTopic(topic);
        snapshot.setStudentsCount(results.size());
        snapshot.setMasteryPercentage(BigDecimal.valueOf(averageScore).setScale(2, RoundingMode.HALF_UP));

        return mapper.toDto(snapshotRepository.save(snapshot));
    }
}