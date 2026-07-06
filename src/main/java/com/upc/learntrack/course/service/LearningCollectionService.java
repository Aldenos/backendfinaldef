package com.upc.learntrack.course.service;

import com.upc.learntrack.course.dto.GroupStatisticDto;
import com.upc.learntrack.course.dto.LearningCollectionDto;
import com.upc.learntrack.course.dto.TopicStatisticDto;
import java.time.LocalDate;
import java.util.List;

public interface LearningCollectionService {
    List<LearningCollectionDto> findAll();
    List<LearningCollectionDto> findAllMyCollections(String teacherEmail);
    List<LearningCollectionDto> findEnrolledCollections(String studentEmail);
    LearningCollectionDto findById(Long id);
    LearningCollectionDto save(LearningCollectionDto dto, String teacherEmail);
    List<TopicStatisticDto> getStatisticsByCollection(String collectionName, LocalDate startDate, LocalDate endDate);
    List<GroupStatisticDto> getGroupsStatistics(String collectionName, LocalDate startDate, LocalDate endDate);
    LearningCollectionDto update(Long id, LearningCollectionDto dto, String teacherEmail);
    void delete(Long id, String teacherEmail);
}
