package com.upc.learntrack.activity.repository;

import com.upc.learntrack.activity.model.LearningActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<LearningActivity, Long> {

    List<LearningActivity> findByTopicId(Long topicId);

    @Query("SELECT a FROM LearningActivity a WHERE LOWER(a.title) = LOWER(:title) AND LOWER(a.topic.name) = LOWER(:topicName)")
    Optional<LearningActivity> findByTitleAndTopicNameIgnoreCase(@Param("title") String title, @Param("topicName") String topicName);

    List<LearningActivity> findByCreatedByEmailAndPersonalTrue(String createdByEmail);
}