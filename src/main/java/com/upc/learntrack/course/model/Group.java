package com.upc.learntrack.course.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "groups", uniqueConstraints = {
       @UniqueConstraint(columnNames = {"teacher_id", "name"})
})
public class Group {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false, length = 100)
   private String name;

   @Column(nullable = false, unique = true, length = 20)
   private String code;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "teacher_id", nullable = false)
   private Teacher teacher;

   @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<Enrollment> enrollments = new ArrayList<>();

   @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<CollectionGroup> collectionGroups = new ArrayList<>();

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "collection_id", nullable = false)
   private LearningCollection learningCollection;

   @Column(name = "created_at", nullable = false, updatable = false)
   private LocalDateTime createdAt;

   @PrePersist
   protected void onCreate() {
       this.createdAt = LocalDateTime.now();
   }

   /**
    * Método para extraer los correos electrónicos de los estudiantes.
    * Navega a través de: Enrollment -> Student -> User -> Email
    */
   public List<String> getStudentEmails() {
       if (this.enrollments == null || this.enrollments.isEmpty()) {
           return Collections.emptyList();
       }
       
       return this.enrollments.stream()
               .map(Enrollment::getStudent)          // Obtenemos el estudiante
               .filter(Objects::nonNull)             // Filtramos estudiantes nulos
               .map(Student::getUser)                // Accedemos al usuario
               .filter(Objects::nonNull)             // Filtramos si el estudiante no tiene usuario
               .map(user -> user.getEmail())         // Obtenemos el email
               .filter(Objects::nonNull)             // Filtramos emails nulos
               .collect(Collectors.toList());
   }
}