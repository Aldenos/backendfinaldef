package com.upc.learntrack.course.repository;

import com.upc.learntrack.course.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByUserId(Long userId);
    Optional<Student> findByUserEmail(String email);
}