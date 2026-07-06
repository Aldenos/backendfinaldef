package com.upc.learntrack.activity.repository;

import com.upc.learntrack.activity.model.StudentFlashcardProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentFlashcardProgressRepository extends JpaRepository<StudentFlashcardProgress, Long> {
    Optional<StudentFlashcardProgress> findByStudentIdAndFlashcardId(Long studentId, Long flashcardId);
}