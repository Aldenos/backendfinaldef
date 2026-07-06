package com.upc.learntrack.course.repository;

import com.upc.learntrack.course.model.CollectionGroup;
import com.upc.learntrack.course.model.CollectionGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionGroupRepository extends JpaRepository<CollectionGroup, CollectionGroupId> {
    List<CollectionGroup> findAllByIdGroupId(Long groupId);
}