package com.upc.learntrack.learningpath.model;

import com.upc.learntrack.course.model.Topic;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conceptual_gaps")
public class ConceptualGap {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "learning_path_id", nullable = false)
   private LearningPath learningPath;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "topic_id", nullable = false)
   private Topic topic;

   @Column(nullable = false, length = 500)
   private String description;

   @Column(nullable = false)
   private boolean resolved = false;
}