package com.upc.learntrack.course.repository;

import com.upc.learntrack.course.model.LearningCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // Importante

@Repository
public interface LearningCollectionRepository extends JpaRepository<LearningCollection, Long> {
    List<LearningCollection> findAllByTeacherId(Long teacherId);

    boolean existsByNameAndTeacherId(String name, Long teacherId);
    boolean existsByCodeAndTeacherId(String code, Long teacherId);

    Optional<LearningCollection> findByName(String name);
    Optional<LearningCollection> findByNameAndTeacherId(String name, Long teacherId);

    Optional<LearningCollection> findByCode(String code);
    Optional<LearningCollection> findByCodeAndTeacherId(String code, Long teacherId);
}