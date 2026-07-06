package com.upc.learntrack.assessment.model;

import com.upc.learntrack.course.model.Group;
import com.upc.learntrack.course.model.Topic;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "statistics_snapshots")
public class StatisticsSnapshot {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "group_id", nullable = false)
   private Group group;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "topic_id", nullable = false)
   private Topic topic;

   @Column(name = "mastery_percentage", nullable = false, precision = 5, scale = 2)
   private BigDecimal masteryPercentage = BigDecimal.ZERO;

   @Column(name = "students_count", nullable = false)
   private Integer studentsCount = 0;

   @Column(name = "generated_at", nullable = false, updatable = false)
   private LocalDateTime generatedAt;

   @PrePersist
   protected void onCreate() {
       this.generatedAt = LocalDateTime.now();
   }
}