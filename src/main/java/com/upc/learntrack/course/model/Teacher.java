package com.upc.learntrack.course.model;

import com.upc.learntrack.iam.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "teachers")
public class Teacher {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "first_name", nullable = false, length = 80)
   private String firstName;

   @Column(name = "last_name", nullable = false, length = 80)
   private String lastName;

   @OneToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id", nullable = false, unique = true)
   private User user;

   @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<Group> groups = new ArrayList<>();

   @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<LearningCollection> collections = new ArrayList<>();
}