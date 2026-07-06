package com.upc.learntrack.assessment.model;

import com.upc.learntrack.activity.model.LearningActivity;
import com.upc.learntrack.course.model.Student;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "activity_results")
public class ActivityResult {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "activity_id", nullable = false)
   private LearningActivity activity;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "student_id", nullable = false)
   private Student student;

   @Column(nullable = false, precision = 5, scale = 2)
   private BigDecimal score = BigDecimal.ZERO;

   @Column(name = "total_questions", nullable = false)
   private Integer totalQuestions = 0;

   @Column(name = "correct_answers", nullable = false)
   private Integer correctAnswers = 0;

   @Column(name = "time_spent_seconds", nullable = false)
   private Integer timeSpentSeconds = 0;

   @Column(nullable = false, length = 20)
   private String status;

   @Column(name = "completed_at")
   private LocalDateTime completedAt;

   @Column(name = "created_at", nullable = false, updatable = false)
   private LocalDateTime createdAt;

   // CASCADA: Al guardar el resultado, se guardan automáticamente todas las respuestas del estudiante
   @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<StudentAnswer> answers = new ArrayList<>();

   @PrePersist
   protected void onCreate() {
       this.createdAt = LocalDateTime.now();
   }
}