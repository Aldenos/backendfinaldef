package com.upc.learntrack.activity.model;

import com.upc.learntrack.course.model.Group;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assignments")
public class Assignment {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "activity_id", nullable = false)
   private LearningActivity activity;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "group_id", nullable = false)
   private Group group;

   private LocalDateTime dueDate;
}