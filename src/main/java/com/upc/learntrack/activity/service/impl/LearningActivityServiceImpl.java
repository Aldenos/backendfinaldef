package com.upc.learntrack.activity.service.impl;

import com.upc.learntrack.activity.dto.LearningActivityDto;
import com.upc.learntrack.activity.dto.QuestionDto;
import com.upc.learntrack.activity.dto.QuestionOptionDto;
import com.upc.learntrack.activity.exception.LearningActivityNotFoundException;
import com.upc.learntrack.activity.mapper.LearningActivityMapper;
import com.upc.learntrack.activity.model.Assignment;
import com.upc.learntrack.activity.model.LearningActivity;
import com.upc.learntrack.activity.repository.ActivityRepository;
import com.upc.learntrack.activity.repository.AssignmentRepository;
import com.upc.learntrack.activity.service.LearningActivityService;
import com.upc.learntrack.course.exception.StudentNotFoundException;
import com.upc.learntrack.course.exception.TopicNotFoundException;
import com.upc.learntrack.course.model.Student;
import com.upc.learntrack.course.model.Topic;
import com.upc.learntrack.course.repository.EnrollmentRepository;
import com.upc.learntrack.course.repository.StudentRepository;
import com.upc.learntrack.course.repository.TopicRepository;
import com.upc.learntrack.iam.model.User;
import com.upc.learntrack.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningActivityServiceImpl implements LearningActivityService {

    private final ActivityRepository activityRepository;
    private final TopicRepository topicRepository;
    private final LearningActivityMapper mapper;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentRepository assignmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LearningActivityDto> findAll() {
        return activityRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LearningActivityDto findById(Long id) {
        LearningActivity activity = activityRepository.findById(id)
                .orElseThrow(() -> new LearningActivityNotFoundException("Actividad no encontrada con ID: " + id));
        return mapper.toDto(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningActivityDto> findByTopicId(Long topicId) {
        return activityRepository.findByTopicId(topicId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningActivityDto> findMyPersonalResources(String userEmail) {
        return activityRepository.findByCreatedByEmailAndPersonalTrue(userEmail).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LearningActivityDto save(Long topicId, LearningActivityDto dto, String userEmail) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException("Tema no encontrado con ID: " + topicId));

        LearningActivity activity = mapper.toEntity(dto);
        activity.setTopic(topic);
        activity.setCreatedByEmail(userEmail);

        if (!isTeacher(userEmail)) {
            activity.setPersonal(true);
            activity.setGeneratedByAi(false);
        } else {
            activity.setPersonal(dto.getPersonal() != null ? dto.getPersonal() : false);
        }

        if (activity.getStatus() == null || activity.getStatus().trim().isEmpty()) {
            activity.setStatus("PUBLISHED");
        }
        if (activity.getGeneratedByAi() == null) {
            activity.setGeneratedByAi(false);
        }

        if (activity.getQuestions() != null) {
            activity.getQuestions().forEach(question -> {
                question.setActivity(activity);
                if (question.getOptions() != null) {
                    question.getOptions().forEach(option -> option.setQuestion(question));
                }
            });
        }

        return mapper.toDto(activityRepository.save(activity));
    }

    @Override
    @Transactional
    public LearningActivityDto update(Long id, LearningActivityDto dto, String userEmail) {
        LearningActivity existingActivity = activityRepository.findById(id)
                .orElseThrow(() -> new LearningActivityNotFoundException("Actividad no encontrada con ID: " + id));

        if (!existingActivity.getCreatedByEmail().equals(userEmail)) {
            throw new AccessDeniedException("No tienes permiso para editar esta actividad porque no eres el autor de la misma.");
        }

        existingActivity.setTitle(dto.getTitle());
        existingActivity.setDescription(dto.getDescription());
        existingActivity.setType(dto.getType());

        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            existingActivity.setStatus(dto.getStatus());
        }
        if (dto.getGeneratedByAi() != null) {
            existingActivity.setGeneratedByAi(dto.getGeneratedByAi());
        }
        if (dto.getPersonal() != null && isTeacher(userEmail)) {
            existingActivity.setPersonal(dto.getPersonal());
        }

        return mapper.toDto(activityRepository.save(existingActivity));
    }

    @Override
    @Transactional
    public void delete(Long id, String userEmail) {
        LearningActivity existingActivity = activityRepository.findById(id)
                .orElseThrow(() -> new LearningActivityNotFoundException("Actividad no encontrada con ID: " + id));

        if (!existingActivity.getCreatedByEmail().equals(userEmail)) {
            throw new AccessDeniedException("No tienes permiso para eliminar esta actividad porque no eres el autor de la misma.");
        }

        activityRepository.delete(existingActivity);
    }

    @Override
    @Transactional(readOnly = true)
    public LearningActivityDto findByIdForStudent(Long id, String userEmail) {
        LearningActivity activity = activityRepository.findById(id)
                .orElseThrow(() -> new LearningActivityNotFoundException("Actividad no encontrada con ID: " + id));
        LearningActivityDto dto = mapper.toDto(activity);

        if (!isTeacher(userEmail)) {
            hideCorrectAnswers(dto);
        }
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningActivityDto> findPendingActivities(String studentEmail) {
        Student student = studentRepository.findByUserEmail(studentEmail)
                .orElseThrow(() -> new StudentNotFoundException("Estudiante no encontrado"));

        List<Long> groupIds = enrollmentRepository.findAllByStudentId(student.getId()).stream()
                .map(e -> e.getGroup().getId())
                .collect(Collectors.toList());

        if (groupIds.isEmpty()) return Collections.emptyList();

        List<Assignment> assignments = assignmentRepository.findByGroupIdInAndDueDateAfterOrDueDateIsNull(groupIds, LocalDateTime.now());

        return assignments.stream()
                .map(Assignment::getActivity)
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    private boolean isTeacher(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        return user != null && "DOCENTE".equals(user.getRole().getName());
    }

    private void hideCorrectAnswers(LearningActivityDto dto) {
        if (dto.getQuestions() != null) {
            for (QuestionDto q : dto.getQuestions()) {
                if (q.getOptions() != null) {
                    for (QuestionOptionDto opt : q.getOptions()) {
                        opt.setCorrect(null);
                    }
                }
            }
        }
    }
}