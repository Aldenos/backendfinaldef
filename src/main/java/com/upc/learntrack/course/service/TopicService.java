package com.upc.learntrack.course.service;

import com.upc.learntrack.course.dto.TopicDto;
import java.util.List;

public interface TopicService {
    List<TopicDto> findAllByCollectionName(String collectionName, String teacherEmail);
    TopicDto findById(Long id);
    TopicDto save(String collectionName, TopicDto dto, String teacherEmail);
    List<TopicDto> findAll();
    List<TopicDto> findPrioritizedTopicsForStudent(String studentEmail);
    TopicDto update(Long id, TopicDto dto, String teacherEmail);
    void delete(Long id, String teacherEmail);
    List<TopicDto> findVisibleTopicsForGroup(String collectionName, Long groupId, String userEmail);
    void assignTopicToGroups(Long topicId, List<Long> groupIds, String teacherEmail);
}