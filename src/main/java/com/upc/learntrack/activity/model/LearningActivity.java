package com.upc.learntrack.activity.model;

import com.upc.learntrack.course.model.Topic;
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
@Table(name = "activities")
public class LearningActivity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   private String title;

   private String description;
   private String type;
   private String status;

   @Column(name = "generated_by_ai")
   private Boolean generatedByAi = false;

   @Column(name = "created_by_email", length = 100)
   private String createdByEmail;

   @Column(name = "created_at", nullable = false, updatable = false)
   private LocalDateTime createdAt;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "topic_id", nullable = false)
   private Topic topic;

   @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<Question> questions = new ArrayList<>();

   @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<Assignment> assignments = new ArrayList<>();

   @PrePersist
   protected void onCreate() {
       this.createdAt = LocalDateTime.now();
   }

   @Column(name = "personal")
   private Boolean personal = false;
}