package com.upc.learntrack.course.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_topic_priorities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupTopicPriority {

    @EmbeddedId
    private GroupTopicId id;

    @Column(nullable = false)
    private boolean priority = false;

    @Column(nullable = false)
    private boolean assigned = true;
}