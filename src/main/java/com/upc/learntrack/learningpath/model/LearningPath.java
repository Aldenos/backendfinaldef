package com.upc.learntrack.learningpath.model;

import com.upc.learntrack.course.model.LearningCollection;
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
@Table(name = "learning_paths", uniqueConstraints = {
       @UniqueConstraint(columnNames = {"student_id", "collection_id"})
})
public class LearningPath {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "student_id", nullable = false)
   private Student student;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "collection_id", nullable = false)
   private LearningCollection learningCollection;

   @Column(name = "target_percentage", nullable = false, precision = 5, scale = 2)
   private BigDecimal targetPercentage = new BigDecimal("70.00");

   @Column(name = "current_percentage", nullable = false, precision = 5, scale = 2)
   private BigDecimal currentPercentage = BigDecimal.ZERO;

   @Column(nullable = false, length = 20)
   private String status;

   @OneToMany(mappedBy = "learningPath", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<ConceptualGap> conceptualGaps = new ArrayList<>();

   @OneToMany(mappedBy = "learningPath", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<PathNode> pathNodes = new ArrayList<>();

   @Column(name = "created_at", nullable = false, updatable = false)
   private LocalDateTime createdAt;

   @Column(name = "updated_at", nullable = false)
   private LocalDateTime updatedAt;

   @PrePersist
   protected void onCreate() {
       this.createdAt = LocalDateTime.now();
       this.updatedAt = LocalDateTime.now();
   }

   @PreUpdate
   protected void onUpdate() {
       this.updatedAt = LocalDateTime.now();
   }
}