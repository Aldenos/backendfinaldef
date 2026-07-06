package com.upc.learntrack.course.repository;

import com.upc.learntrack.course.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
   List<Topic> findAllByLearningCollectionIdOrderByOrderIdxAsc(Long learningCollectionId);
   boolean existsByNameAndLearningCollectionId(String name, Long learningCollectionId);
   List<Topic> findAllByLearningCollectionId(Long learningCollectionId);
   Optional<Topic> findByName(String name);

   /**
    * Devuelve los temas visibles para un grupo en una colección.
    * Si el tema no tiene ninguna restricción en group_topic_priorities → aplica a todos los grupos.
    * Si tiene entradas → solo aplica a los grupos donde assigned = true.
    */
   @Query("""
       SELECT t FROM Topic t
       WHERE t.learningCollection.id = :collectionId
       AND (
           NOT EXISTS (SELECT gtp FROM GroupTopicPriority gtp WHERE gtp.id.topicId = t.id)
           OR EXISTS (SELECT gtp FROM GroupTopicPriority gtp WHERE gtp.id.topicId = t.id AND gtp.id.groupId = :groupId AND gtp.assigned = true)
       )
       ORDER BY t.orderIdx ASC
       """)
   List<Topic> findVisibleTopicsForGroup(@Param("collectionId") Long collectionId, @Param("groupId") Long groupId);
}