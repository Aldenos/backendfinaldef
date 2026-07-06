package com.upc.learntrack.course.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "enrollments", uniqueConstraints = {
       @UniqueConstraint(columnNames = {"student_id", "group_id"})
})
public class Enrollment {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "student_id", nullable = false)
   private Student student;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "group_id", nullable = false)
   private Group group;

   @Column(name = "enrolled_at", nullable = false, updatable = false)
   private LocalDateTime enrolledAt;

   @PrePersist
   protected void onCreate() {
       this.enrolledAt = LocalDateTime.now();
   }
}