package com.upc.learntrack.learningpath.repository;

import com.upc.learntrack.learningpath.model.PathNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PathNodeRepository extends JpaRepository<PathNode, Long> {
    List<PathNode> findAllByLearningPathIdOrderByOrderIdxAsc(Long learningPathId);
}