package com.upc.learntrack.course.repository;

import com.upc.learntrack.course.model.GroupJoinCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupJoinCodeRepository extends JpaRepository<GroupJoinCode, Long> {
    Optional<GroupJoinCode> findByCode(String code);
    boolean existsByCode(String code);
}