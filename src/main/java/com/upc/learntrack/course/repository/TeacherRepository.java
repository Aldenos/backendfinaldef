package com.upc.learntrack.course.repository;

import com.upc.learntrack.course.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUserEmail(String email);
    boolean existsByUserId(Long userId);
}