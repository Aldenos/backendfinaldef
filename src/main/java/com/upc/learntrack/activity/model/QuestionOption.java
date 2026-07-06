package com.upc.learntrack.activity.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_options")
public class QuestionOption {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   private String text;

   @Column(nullable = false)
   private Boolean correct;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "question_id", nullable = false)
   private Question question;
}