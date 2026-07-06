package com.upc.learntrack.course.repository;

import com.upc.learntrack.course.model.GroupTopicId;
import com.upc.learntrack.course.model.GroupTopicPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupTopicPriorityRepository extends JpaRepository<GroupTopicPriority, GroupTopicId> {

    @Query("SELECT gtp FROM GroupTopicPriority gtp WHERE gtp.id.groupId IN :groupIds AND gtp.priority = true")
    List<GroupTopicPriority> findAllByGroupIdInAndPriorityTrue(@Param("groupIds") List<Long> groupIds);

    @Query("SELECT gtp FROM GroupTopicPriority gtp WHERE gtp.id.topicId = :topicId")
    List<GroupTopicPriority> findAllByTopicId(@Param("topicId") Long topicId);
}