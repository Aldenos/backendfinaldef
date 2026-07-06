package com.upc.learntrack.learningpath.repository;

import com.upc.learntrack.learningpath.model.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {
   List<LearningPath> findAllByStudentId(Long studentId);   
   Optional<LearningPath> findByStudentIdAndLearningCollectionId(Long studentId, Long collectionId);
}