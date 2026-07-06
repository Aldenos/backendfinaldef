package com.upc.learntrack.learningpath.model;

import com.upc.learntrack.course.model.Topic;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "path_nodes")
public class PathNode {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "learning_path_id", nullable = false)
   private LearningPath learningPath;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "topic_id", nullable = false)
   private Topic topic;

   @Column(name = "order_idx", nullable = false)
   private Integer orderIdx = 0;

   @Column(nullable = false)
   private Boolean completed = false;

   @Column(name = "mastery_score", nullable = false, precision = 5, scale = 2)
   private BigDecimal masteryScore = BigDecimal.ZERO;
}