package com.upc.learntrack.activity.model;

import com.upc.learntrack.course.model.Student;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_flashcard_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "flashcard_id"})
})
public class StudentFlashcardProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashcard_id", nullable = false)
    private Flashcard flashcard;

    @Column(nullable = false)
    private Boolean recalled = false;

    @Column(name = "last_reviewed_at")
    private LocalDateTime lastReviewedAt;

    @PreUpdate
    @PrePersist
    protected void onUpdate() {
        lastReviewedAt = LocalDateTime.now();
    }
}