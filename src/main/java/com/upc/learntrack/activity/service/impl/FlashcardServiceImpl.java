package com.upc.learntrack.activity.service.impl;

import com.upc.learntrack.activity.dto.CreateFlashcardDto;
import com.upc.learntrack.activity.dto.CreateFlashcardSetDto;
import com.upc.learntrack.activity.dto.FlashcardDto;
import com.upc.learntrack.activity.dto.FlashcardSetDto;
import com.upc.learntrack.activity.exception.FlashcardNotFoundException;
import com.upc.learntrack.activity.exception.ResourceNotFoundException;
import com.upc.learntrack.activity.exception.UnauthorizedAccessException;
import com.upc.learntrack.activity.model.Flashcard;
import com.upc.learntrack.activity.model.FlashcardSet;
import com.upc.learntrack.activity.model.StudentFlashcardProgress;
import com.upc.learntrack.activity.repository.FlashcardRepository;
import com.upc.learntrack.activity.repository.FlashcardSetRepository;
import com.upc.learntrack.activity.repository.StudentFlashcardProgressRepository;
import com.upc.learntrack.activity.service.FlashcardService;
import com.upc.learntrack.course.exception.StudentNotFoundException;
import com.upc.learntrack.course.exception.TopicNotFoundException;
import com.upc.learntrack.course.model.Student;
import com.upc.learntrack.course.model.Topic;
import com.upc.learntrack.course.repository.StudentRepository;
import com.upc.learntrack.course.repository.TopicRepository;
import com.upc.learntrack.iam.repository.UserRepository;
import com.upc.learntrack.learningpath.repository.LearningPathRepository;
import com.upc.learntrack.learningpath.repository.PathNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlashcardServiceImpl implements FlashcardService {

    private final FlashcardSetRepository flashcardSetRepository;
    private final FlashcardRepository flashcardRepository;
    private final StudentFlashcardProgressRepository progressRepository;
    private final StudentRepository studentRepository;
    private final TopicRepository topicRepository;
    private final LearningPathRepository learningPathRepository;
    private final PathNodeRepository pathNodeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public FlashcardSetDto getFlashcardSetByTopic(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException("Tema no encontrado"));
        List<FlashcardSet> sets = flashcardSetRepository.findByTopicId(topicId);
        if (sets.isEmpty()) {
            throw new ResourceNotFoundException("Contenido no disponible para este tema");
        }
        FlashcardSet set = sets.getFirst();
        FlashcardSetDto dto = new FlashcardSetDto();
        dto.setId(set.getId());
        dto.setTitle(set.getTitle());
        dto.setTopicId(topicId);
        dto.setTopicName(topic.getName());
        List<FlashcardDto> flashcardDtos = set.getFlashcards().stream()
                .map(f -> {
                    FlashcardDto fd = new FlashcardDto();
                    fd.setId(f.getId());
                    fd.setFront(f.getFront());
                    fd.setBack(f.getBack());
                    return fd;
                }).collect(Collectors.toList());
        dto.setFlashcards(flashcardDtos);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardSetDto> getFlashcardSetsByTopic(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException("Tema no encontrado con id: " + topicId));
        List<FlashcardSet> sets = flashcardSetRepository.findByTopicId(topicId);
        if (sets.isEmpty()) {
            throw new ResourceNotFoundException("No hay flashcards disponibles para este tema");
        }
        return sets.stream().map(set -> {
            FlashcardSetDto dto = new FlashcardSetDto();
            dto.setId(set.getId());
            dto.setTitle(set.getTitle());
            dto.setTopicId(topicId);
            dto.setTopicName(topic.getName());
            List<FlashcardDto> flashcardDtos = set.getFlashcards().stream()
                    .map(f -> {
                        FlashcardDto fd = new FlashcardDto();
                        fd.setId(f.getId());
                        fd.setFront(f.getFront());
                        fd.setBack(f.getBack());
                        return fd;
                    }).collect(Collectors.toList());
            dto.setFlashcards(flashcardDtos);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void recordProgress(Long flashcardId, Boolean recalled, String studentEmail) {
        Student student = studentRepository.findByUserEmail(studentEmail)
                .orElseThrow(() -> new StudentNotFoundException("Estudiante no encontrado"));
        Flashcard flashcard = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new FlashcardNotFoundException("Flashcard no encontrada con id: " + flashcardId));
        StudentFlashcardProgress progress = progressRepository
                .findByStudentIdAndFlashcardId(student.getId(), flashcardId)
                .orElse(new StudentFlashcardProgress());
        progress.setStudent(student);
        progress.setFlashcard(flashcard);
        progress.setRecalled(recalled);
        progressRepository.save(progress);

        if (Boolean.TRUE.equals(recalled)) {
            Topic topic = flashcard.getFlashcardSet().getTopic();
            learningPathRepository.findByStudentIdAndLearningCollectionId(student.getId(), topic.getLearningCollection().getId())
                    .flatMap(path -> pathNodeRepository.findAllByLearningPathIdOrderByOrderIdxAsc(path.getId()).stream()
                            .filter(node -> node.getTopic().getId().equals(topic.getId()))
                            .findFirst())
                    .ifPresent(node -> {
                        BigDecimal newScore = node.getMasteryScore().add(BigDecimal.valueOf(5));
                        if (newScore.compareTo(BigDecimal.valueOf(100)) > 0) {
                            newScore = BigDecimal.valueOf(100);
                        }
                        node.setMasteryScore(newScore);
                        pathNodeRepository.save(node);
                    });
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardSetDto> getAllFlashcardSets(String studentEmail) {
        return flashcardSetRepository.findAll().stream()
                .map(set -> {
                    FlashcardSetDto dto = new FlashcardSetDto();
                    dto.setId(set.getId());
                    dto.setTitle(set.getTitle());
                    dto.setTopicId(set.getTopic().getId());
                    dto.setTopicName(set.getTopic().getName());
                    List<FlashcardDto> flashcards = set.getFlashcards().stream()
                            .map(f -> {
                                FlashcardDto fd = new FlashcardDto();
                                fd.setId(f.getId());
                                fd.setFront(f.getFront());
                                fd.setBack(f.getBack());
                                return fd;
                            }).collect(Collectors.toList());
                    dto.setFlashcards(flashcards);
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FlashcardSetDto createFlashcardSet(Long topicId, CreateFlashcardSetDto dto, String userEmail) {
        validateUserIsTeacher(userEmail);
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException("Tema no encontrado"));
        FlashcardSet set = new FlashcardSet();
        set.setTitle(dto.getTitle());
        set.setTopic(topic);
        set.setCreatedByEmail(userEmail);
        FlashcardSet saved = flashcardSetRepository.save(set);

        if (dto.getFlashcards() != null) {
            for (CreateFlashcardDto cardDto : dto.getFlashcards()) {
                addFlashcardToSet(saved.getId(), cardDto, userEmail);
            }
        }
        return getFlashcardSetByTopic(topicId);
    }

    @Override
    @Transactional
    public FlashcardSetDto addFlashcardToSet(Long setId, CreateFlashcardDto dto, String userEmail) {
        validateUserIsTeacher(userEmail);
        FlashcardSet set = flashcardSetRepository.findById(setId)
                .orElseThrow(() -> new RuntimeException("Set de flashcards no encontrado"));
        validateUserCanModifySet(set, userEmail);
        Flashcard flashcard = new Flashcard();
        flashcard.setFront(dto.getFront());
        flashcard.setBack(dto.getBack());
        flashcard.setFlashcardSet(set);
        flashcardRepository.save(flashcard);
        return getFlashcardSetByTopic(set.getTopic().getId());
    }

    @Override
    @Transactional
    public void deleteFlashcard(Long flashcardId, String userEmail) {
        validateUserIsTeacher(userEmail);
        Flashcard flashcard = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new FlashcardNotFoundException("Flashcard no encontrada"));
        validateUserCanModifySet(flashcard.getFlashcardSet(), userEmail);
        flashcardRepository.delete(flashcard);
    }

    private void validateUserIsTeacher(String userEmail) {
        userRepository.findByEmail(userEmail)
                .ifPresent(user -> {
                    if (!"DOCENTE".equals(user.getRole().getName())) {
                        throw new UnauthorizedAccessException("Solo los docentes pueden crear o modificar flashcards");
                    }
                });
    }

    private void validateUserCanModifySet(FlashcardSet set, String userEmail) {
        if (!userEmail.equals(set.getCreatedByEmail())) {
            throw new UnauthorizedAccessException("No tienes permiso para modificar este set de flashcards");
        }
    }
}