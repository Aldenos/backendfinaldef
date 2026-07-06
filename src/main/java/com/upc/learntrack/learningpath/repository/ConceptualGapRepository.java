package com.upc.learntrack.learningpath.repository;

import com.upc.learntrack.learningpath.model.ConceptualGap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConceptualGapRepository extends JpaRepository<ConceptualGap, Long> {
    List<ConceptualGap> findAllByLearningPathId(Long learningPathId);
}