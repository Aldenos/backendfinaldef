package com.upc.learntrack.course.service;

import com.upc.learntrack.course.dto.CollectionGroupDto;
import com.upc.learntrack.course.dto.LinkCollectionGroupRequest;

import java.util.List;

public interface CollectionGroupService {
    List<CollectionGroupDto> findAllByGroup(Long groupId);
    void linkByName(LinkCollectionGroupRequest request);
    void delete(Long collectionId, Long groupId);
    void moveGroup(Long groupId, Long targetCollectionId);
}