package com.upc.learntrack.course.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "collections", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"teacher_id", "name"}),
        @UniqueConstraint(columnNames = {"teacher_id", "code"})
})
public class LearningCollection {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false, length = 20)
   private String code;

   @Column(nullable = false, length = 120)
   private String name;

   @Column(columnDefinition = "TEXT")
   private String description;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "teacher_id", nullable = false)
   private Teacher teacher;

   @OneToMany(mappedBy = "learningCollection", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<Topic> topics = new ArrayList<>();

   @OneToMany(mappedBy = "learningCollection", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<Group> groups = new ArrayList<>();

   // Déjalo por compatibilidad temporal
   @OneToMany(mappedBy = "learningCollection", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<CollectionGroup> collectionGroups = new ArrayList<>();

   @Column(name = "created_at", nullable = false, updatable = false)
   private LocalDateTime createdAt;

   @PrePersist
   protected void onCreate() {
      this.createdAt = LocalDateTime.now();
   }
}