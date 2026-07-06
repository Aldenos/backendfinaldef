package com.upc.learntrack.assessment.model;

import com.upc.learntrack.course.model.LearningCollection;
import com.upc.learntrack.course.model.Group;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pdf_reports")
public class PdfReport {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "group_id", nullable = false)
   private Group group;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "collection_id", nullable = false)
   private LearningCollection learningCollection;

   @Column(name = "file_url", nullable = false, length = 255)
   private String fileUrl;

   @Column(name = "sent_to", length = 100)
   private String sentTo;

   @Column(name = "created_at", nullable = false, updatable = false)
   private LocalDateTime createdAt;

   // Campo para almacenar el PDF
   @Column(name = "pdf_content", columnDefinition = "bytea")
   private byte[] pdfContent;

   @PrePersist
   protected void onCreate() {
      this.createdAt = LocalDateTime.now();
   }
}