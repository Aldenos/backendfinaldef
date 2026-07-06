package com.upc.learntrack.assessment.repository;

import com.upc.learntrack.assessment.model.ActivityResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityResultRepository extends JpaRepository<ActivityResult, Long> {

    // Método necesario para obtener todos los resultados de un estudiante
    List<ActivityResult> findAllByStudentId(Long studentId);

    @Query("SELECT ar FROM ActivityResult ar WHERE ar.student.id = :studentId AND ar.activity.type = :type")
    List<ActivityResult> findAllByStudentIdAndActivityType(@Param("studentId") Long studentId, @Param("type") String type);

    @Query("SELECT ar FROM ActivityResult ar WHERE ar.student.id = :studentId AND ar.activity.topic.id = :topicId")
    List<ActivityResult> findAllByStudentIdAndTopicId(@Param("studentId") Long studentId, @Param("topicId") Long topicId);

    // Método necesario para obtener resultados de múltiples estudiantes en un tema
    @Query("SELECT ar FROM ActivityResult ar WHERE ar.student.id IN :studentIds AND ar.activity.topic.id = :topicId")
    List<ActivityResult> findAllByStudentIdsAndTopicId(@Param("studentIds") List<Long> studentIds, @Param("topicId") Long topicId);
}