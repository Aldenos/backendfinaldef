package com.upc.learntrack.assessment.service.impl;

import com.upc.learntrack.activity.model.LearningActivity;
import com.upc.learntrack.activity.model.Question;
import com.upc.learntrack.activity.model.QuestionOption;
import com.upc.learntrack.activity.repository.ActivityRepository;
import com.upc.learntrack.activity.repository.AssignmentRepository;
import com.upc.learntrack.assessment.dto.*;
import com.upc.learntrack.assessment.exception.ActivityResultNotFoundException;
import com.upc.learntrack.assessment.mapper.ActivityResultMapper;
import com.upc.learntrack.assessment.model.ActivityResult;
import com.upc.learntrack.assessment.model.StudentAnswer;
import com.upc.learntrack.assessment.repository.ActivityResultRepository;
import com.upc.learntrack.assessment.service.ActivityResultService;
import com.upc.learntrack.course.exception.StudentNotFoundException;
import com.upc.learntrack.course.model.Student;
import com.upc.learntrack.course.repository.EnrollmentRepository;
import com.upc.learntrack.course.repository.StudentRepository;
import com.upc.learntrack.iam.model.User;
import com.upc.learntrack.iam.repository.UserRepository;
import com.upc.learntrack.learningpath.model.ConceptualGap;
import com.upc.learntrack.learningpath.repository.ConceptualGapRepository;
import com.upc.learntrack.learningpath.repository.LearningPathRepository;
import com.upc.learntrack.learningpath.repository.PathNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityResultServiceImpl implements ActivityResultService {

    private final ActivityResultRepository activityResultRepository;
    private final ActivityRepository activityRepository;
    private final StudentRepository studentRepository;
    private final AssignmentRepository assignmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LearningPathRepository learningPathRepository;
    private final ConceptualGapRepository conceptualGapRepository;
    private final PathNodeRepository pathNodeRepository;
    private final ActivityResultMapper mapper;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ActivityResultDto> findAll() {
        return activityResultRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityResultDto findById(Long id) {
        return activityResultRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ActivityResultNotFoundException("Resultado no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityResultDto> findAllMyResults(String studentEmail, String type, Long topicId, String sort) {
        Student student = studentRepository.findByUserEmail(studentEmail)
                .orElseThrow(() -> new StudentNotFoundException("Estudiante no encontrado con email: " + studentEmail));

        List<ActivityResult> results;
        if (type != null && !type.isBlank()) {
            results = activityResultRepository.findAllByStudentIdAndActivityType(student.getId(), type);
        } else if (topicId != null) {
            results = activityResultRepository.findAllByStudentIdAndTopicId(student.getId(), topicId);
        } else {
            results = activityResultRepository.findAllByStudentId(student.getId());
        }

        if ("asc".equalsIgnoreCase(sort)) {
            results.sort(Comparator.comparing(ActivityResult::getCompletedAt));
        } else if ("desc".equalsIgnoreCase(sort)) {
            results.sort(Comparator.comparing(ActivityResult::getCompletedAt).reversed());
        }

        return results.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ActivityResultDetailedResponseDto submit(String topicName, String activityTitle, ActivityResultSubmitDto dto, String studentEmail) {
        Student student = studentRepository.findByUserEmail(studentEmail)
                .orElseThrow(() -> new StudentNotFoundException("Estudiante no encontrado con email: " + studentEmail));

        LearningActivity activity = activityRepository.findByTitleAndTopicNameIgnoreCase(activityTitle, topicName)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada para el tema '" + topicName + "' y título '" + activityTitle + "'"));

        // NUEVA VALIDACIÓN: todas las preguntas deben ser respondidas (US14, escenario 3)
        int totalQuestions = activity.getQuestions().size();
        int answeredQuestions = dto.getAnswers().size();
        if (answeredQuestions != totalQuestions) {
            throw new IllegalArgumentException("Debes responder todas las preguntas del quiz. Respondiste " + answeredQuestions + " de " + totalQuestions + " preguntas.");
        }

        // Validar fecha límite de la actividad para los grupos del estudiante
        assignmentRepository.findByActivityId(activity.getId()).forEach(assignment -> {
            boolean isInGroup = enrollmentRepository.existsByStudentIdAndGroupId(student.getId(), assignment.getGroup().getId());
            if (isInGroup && assignment.getDueDate() != null && LocalDateTime.now().isAfter(assignment.getDueDate())) {
                throw new AccessDeniedException("La fecha límite para esta actividad ha expirado. No puedes enviarla.");
            }
        });

        ActivityResult result = new ActivityResult();
        result.setActivity(activity);
        result.setStudent(student);
        result.setTimeSpentSeconds(dto.getTimeSpentSeconds() != null ? dto.getTimeSpentSeconds() : 0);
        result.setStatus("COMPLETED");
        result.setCompletedAt(LocalDateTime.now());
        result.setAnswers(new ArrayList<>());

        int correctCount = 0;
        for (StudentAnswerSubmitDto ansDto : dto.getAnswers()) {
            Question question = activity.getQuestions().stream()
                    .filter(q -> q.getStatement().trim().equalsIgnoreCase(ansDto.getQuestionText().trim()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("La pregunta '" + ansDto.getQuestionText() + "' no pertenece a esta actividad."));

            QuestionOption option = question.getOptions().stream()
                    .filter(o -> o.getText().trim().equalsIgnoreCase(ansDto.getSelectedOptionText().trim()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Opción no válida para la pregunta."));

            StudentAnswer answer = new StudentAnswer();
            answer.setResult(result);
            answer.setQuestion(question);
            answer.setSelectedOption(option);
            answer.setIsCorrect(option.getCorrect());

            result.getAnswers().add(answer);
            if (option.getCorrect()) {
                correctCount++;
            }
        }

        int totalQuestionsCount = activity.getQuestions().size();
        result.setTotalQuestions(totalQuestionsCount);
        result.setCorrectAnswers(correctCount);
        double finalScore = totalQuestionsCount == 0 ? 0.0 : ((double) correctCount / totalQuestionsCount) * 100.0;
        result.setScore(BigDecimal.valueOf(finalScore));

        Long collectionId = activity.getTopic().getLearningCollection().getId();

        if (finalScore < 70.0) {
            learningPathRepository.findByStudentIdAndLearningCollectionId(student.getId(), collectionId)
                    .ifPresent(path -> {
                        ConceptualGap gap = new ConceptualGap();
                        gap.setLearningPath(path);
                        gap.setTopic(activity.getTopic());
                        gap.setDescription("El estudiante obtuvo " + finalScore + "% en la actividad '" + activity.getTitle() + "'. Requiere refuerzo.");
                        gap.setResolved(false);
                        conceptualGapRepository.save(gap);
                    });
        } else {
            learningPathRepository.findByStudentIdAndLearningCollectionId(student.getId(), collectionId)
                    .flatMap(path -> pathNodeRepository.findAllByLearningPathIdOrderByOrderIdxAsc(path.getId()).stream()
                            .filter(node -> node.getTopic().getId().equals(activity.getTopic().getId()))
                            .findFirst())
                    .ifPresentOrElse(node -> {
                        node.setCompleted(true);
                        node.setMasteryScore(BigDecimal.valueOf(finalScore));
                        pathNodeRepository.save(node);
                    }, () -> log.warn("No se encontró PathNode para el estudiante {} en el tema {}", student.getId(), activity.getTopic().getId()));
        }

        ActivityResult saved = activityResultRepository.save(result);

        ActivityResultDetailedResponseDto response = new ActivityResultDetailedResponseDto();
        response.setId(saved.getId());
        response.setActivityTitle(saved.getActivity().getTitle());
        response.setScore(saved.getScore());
        response.setTotalQuestions(saved.getTotalQuestions());
        response.setCorrectAnswers(saved.getCorrectAnswers());
        response.setTimeSpentSeconds(saved.getTimeSpentSeconds());
        response.setStatus(saved.getStatus());
        response.setCompletedAt(saved.getCompletedAt());

        List<AnswerDetailDto> answerDetails = saved.getAnswers().stream().map(a -> {
            AnswerDetailDto ad = new AnswerDetailDto();
            ad.setQuestionText(a.getQuestion().getStatement());
            ad.setSelectedOptionText(a.getSelectedOption().getText());
            String correctText = a.getQuestion().getOptions().stream()
                    .filter(QuestionOption::getCorrect)
                    .map(QuestionOption::getText)
                    .findFirst().orElse("");
            ad.setCorrectOptionText(correctText);
            ad.setExplanation(a.getQuestion().getExplanation());
            ad.setIsCorrect(a.getIsCorrect());
            return ad;
        }).collect(Collectors.toList());
        response.setAnswers(answerDetails);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityResultDetailDto findResultDetail(Long resultId, String userEmail) {
        ActivityResult result = activityResultRepository.findById(resultId)
                .orElseThrow(() -> new ActivityResultNotFoundException("Resultado no encontrado con id: " + resultId));

        boolean isOwner = result.getStudent().getUser().getEmail().equals(userEmail);
        boolean isTeacher = isTeacher(userEmail);
        if (!isOwner && !isTeacher) {
            throw new AccessDeniedException("No tiene permisos para ver este resultado");
        }

        ActivityResultDetailDto dto = new ActivityResultDetailDto();
        dto.setId(result.getId());
        dto.setActivityTitle(result.getActivity().getTitle());
        dto.setStudentEmail(result.getStudent().getUser().getEmail());
        dto.setScore(result.getScore());
        dto.setTotalQuestions(result.getTotalQuestions());
        dto.setCorrectAnswers(result.getCorrectAnswers());
        dto.setTimeSpentSeconds(result.getTimeSpentSeconds());
        dto.setStatus(result.getStatus());
        dto.setCompletedAt(result.getCompletedAt());

        List<AnswerDetailDto> answerDetails = result.getAnswers().stream().map(a -> {
            AnswerDetailDto ad = new AnswerDetailDto();
            ad.setQuestionText(a.getQuestion().getStatement());
            ad.setSelectedOptionText(a.getSelectedOption().getText());
            String correctText = a.getQuestion().getOptions().stream()
                    .filter(QuestionOption::getCorrect)
                    .map(QuestionOption::getText)
                    .findFirst().orElse("");
            ad.setCorrectOptionText(correctText);
            ad.setExplanation(a.getQuestion().getExplanation());
            ad.setIsCorrect(a.getIsCorrect());
            return ad;
        }).collect(Collectors.toList());
        dto.setAnswers(answerDetails);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimelinePointDto> getTimeline(String studentEmail, Long topicId, LocalDate startDate, LocalDate endDate) {
        Student student = studentRepository.findByUserEmail(studentEmail)
                .orElseThrow(() -> new StudentNotFoundException("Estudiante no encontrado con email: " + studentEmail));

        List<ActivityResult> results = activityResultRepository.findAllByStudentId(student.getId());

        if (topicId != null) {
            results = results.stream()
                    .filter(r -> r.getActivity().getTopic().getId().equals(topicId))
                    .toList();
        }
        if (startDate != null) {
            results = results.stream()
                    .filter(r -> !r.getCompletedAt().toLocalDate().isBefore(startDate))
                    .toList();
        }
        if (endDate != null) {
            results = results.stream()
                    .filter(r -> !r.getCompletedAt().toLocalDate().isAfter(endDate))
                    .toList();
        }

        return results.stream()
                .sorted(Comparator.comparing(ActivityResult::getCompletedAt))
                .map(r -> {
                    TimelinePointDto dto = new TimelinePointDto();
                    dto.setDate(r.getCompletedAt());
                    dto.setScore(r.getScore());
                    dto.setActivityTitle(r.getActivity().getTitle());
                    dto.setTopicName(r.getActivity().getTopic().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private boolean isTeacher(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null && "DOCENTE".equals(user.getRole().getName());
    }
}