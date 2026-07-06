package com.upc.learntrack.course.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "collection_groups")
public class CollectionGroup {

    @EmbeddedId
    @Builder.Default
    private CollectionGroupId id = new CollectionGroupId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("collectionId")
    @JoinColumn(name = "collection_id", nullable = false)
    private LearningCollection learningCollection;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupId")
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "linked_at", nullable = false, updatable = false)
    private LocalDateTime linkedAt;

    @PrePersist
    protected void onCreate() {
        this.linkedAt = LocalDateTime.now();
    }
}