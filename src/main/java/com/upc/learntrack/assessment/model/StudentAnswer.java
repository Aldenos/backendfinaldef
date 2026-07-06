package com.upc.learntrack.assessment.model;

import com.upc.learntrack.activity.model.Question;
import com.upc.learntrack.activity.model.QuestionOption;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_answers")
public class StudentAnswer {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "result_id", nullable = false)
   private ActivityResult result;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "question_id", nullable = false)
   private Question question;

   // Enlazamos directamente con la opción que el estudiante eligió
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "selected_option_id", nullable = false)
   private QuestionOption selectedOption;

   @Column(name = "is_correct", nullable = false)
   private Boolean isCorrect = false;

   @Column(name = "answered_at", nullable = false, updatable = false)
   private LocalDateTime answeredAt;

   @PrePersist
   protected void onCreate() {
       this.answeredAt = LocalDateTime.now();
   }
}