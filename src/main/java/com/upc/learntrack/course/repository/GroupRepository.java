package com.upc.learntrack.course.repository;

import com.upc.learntrack.course.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByName(String name);
    Optional<Group> findByCode(String code);

    boolean existsByCode(String code);

    List<Group> findAllByTeacherId(Long teacherId);
    List<Group> findAllByTeacherIdAndLearningCollectionId(Long teacherId, Long collectionId);

    boolean existsByTeacherIdAndName(Long teacherId, String name);
    boolean existsByLearningCollectionIdAndCode(Long collectionId, String code);
    boolean existsByLearningCollectionIdAndName(Long collectionId, String name);
}