package com.upc.learntrack.activity.model;

import com.upc.learntrack.course.model.SubTopic;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "questions")
public class Question {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false, length = 500)
   private String statement;

   private String explanation;
   private Integer orderIdx;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "activity_id", nullable = false)
   private LearningActivity activity;

   @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<QuestionOption> options = new ArrayList<>();

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "subtopic_id")
   private SubTopic subTopic;
}