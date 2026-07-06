package com.upc.learntrack.course.model;

import com.upc.learntrack.iam.model.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
public class Student {

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
}