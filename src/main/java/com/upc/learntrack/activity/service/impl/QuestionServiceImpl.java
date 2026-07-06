package com.upc.learntrack.activity.service.impl;

import com.upc.learntrack.activity.dto.QuestionDto;
import com.upc.learntrack.activity.dto.QuestionOptionDto;
import com.upc.learntrack.activity.exception.LearningActivityNotFoundException;
import com.upc.learntrack.activity.exception.QuestionNotFoundException;
import com.upc.learntrack.activity.exception.QuestionOptionNotFoundException;
import com.upc.learntrack.activity.mapper.QuestionMapper;
import com.upc.learntrack.activity.mapper.QuestionOptionMapper;
import com.upc.learntrack.activity.model.LearningActivity;
import com.upc.learntrack.activity.model.Question;
import com.upc.learntrack.activity.model.QuestionOption;
import com.upc.learntrack.activity.repository.ActivityRepository;
import com.upc.learntrack.activity.repository.QuestionOptionRepository;
import com.upc.learntrack.activity.repository.QuestionRepository;
import com.upc.learntrack.activity.service.QuestionService;
import com.upc.learntrack.iam.model.User;
import com.upc.learntrack.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper optionMapper;

    private boolean isTeacher(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        return user != null && "DOCENTE".equals(user.getRole().getName());
    }

    private boolean isAuthor(Long activityId, String userEmail) {
        LearningActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new LearningActivityNotFoundException("Actividad no encontrada"));
        return activity.getCreatedByEmail().equals(userEmail);
    }

    private void checkPermission(Long activityId, String userEmail) {
        if (!isTeacher(userEmail) && !isAuthor(activityId, userEmail)) {
            throw new AccessDeniedException("No tienes permiso para modificar esta actividad");
        }
    }

    @Override
    @Transactional
    public QuestionDto createQuestion(Long activityId, QuestionDto dto, String userEmail) {
        checkPermission(activityId, userEmail);
        LearningActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new LearningActivityNotFoundException("Actividad no encontrada"));
        Question question = questionMapper.toEntity(dto);
        question.setActivity(activity);
        question.setOrderIdx(activity.getQuestions().size()); // al final
        Question saved = questionRepository.save(question);
        return questionMapper.toDto(saved);
    }

    @Override
    @Transactional
    public QuestionDto updateQuestion(Long questionId, QuestionDto dto, String userEmail) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Pregunta no encontrada"));
        checkPermission(question.getActivity().getId(), userEmail);
        question.setStatement(dto.getStatement());
        question.setExplanation(dto.getExplanation());
        question.setOrderIdx(dto.getOrderIdx());
        // Nota: las opciones se manejan aparte
        return questionMapper.toDto(questionRepository.save(question));
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId, String userEmail) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Pregunta no encontrada"));
        checkPermission(question.getActivity().getId(), userEmail);
        questionRepository.delete(question);
    }

    @Override
    @Transactional
    public QuestionOptionDto createOption(Long questionId, QuestionOptionDto dto, String userEmail) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Pregunta no encontrada"));
        checkPermission(question.getActivity().getId(), userEmail);
        QuestionOption option = optionMapper.toEntity(dto);
        option.setQuestion(question);
        return optionMapper.toDto(optionRepository.save(option));
    }

    @Override
    @Transactional
    public QuestionOptionDto updateOption(Long optionId, QuestionOptionDto dto, String userEmail) {
        QuestionOption option = optionRepository.findById(optionId)
                .orElseThrow(() -> new QuestionOptionNotFoundException("Opción no encontrada"));
        checkPermission(option.getQuestion().getActivity().getId(), userEmail);
        option.setText(dto.getText());
        option.setCorrect(dto.getCorrect());
        return optionMapper.toDto(optionRepository.save(option));
    }

    @Override
    @Transactional
    public void deleteOption(Long optionId, String userEmail) {
        QuestionOption option = optionRepository.findById(optionId)
                .orElseThrow(() -> new QuestionOptionNotFoundException("Opción no encontrada"));
        checkPermission(option.getQuestion().getActivity().getId(), userEmail);
        optionRepository.delete(option);
    }
}