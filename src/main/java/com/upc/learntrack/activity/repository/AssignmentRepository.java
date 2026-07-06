package com.upc.learntrack.activity.repository;

import com.upc.learntrack.activity.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    boolean existsByActivityIdAndGroupId(Long activityId, Long groupId);

    List<Assignment> findByActivityId(Long activityId);
    List<Assignment> findByGroupIdInAndDueDateAfterOrDueDateIsNull(List<Long> groupIds, LocalDateTime now);
}