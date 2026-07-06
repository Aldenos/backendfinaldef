package com.upc.learntrack.course.dto;
import lombok.Data;

@Data
public class MoveGroupRequest {
    private Long groupId;
    private Long targetCollectionId;
}