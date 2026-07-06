package com.upc.learntrack.course.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionGroupDto {
    private Long learningCollectionId;
    private Long groupId;
}