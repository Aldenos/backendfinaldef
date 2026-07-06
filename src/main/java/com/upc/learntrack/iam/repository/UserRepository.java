package com.upc.learntrack.iam.repository;

import com.upc.learntrack.iam.model.User;
import com.upc.learntrack.iam.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAllByStatus(UserStatus status);
}