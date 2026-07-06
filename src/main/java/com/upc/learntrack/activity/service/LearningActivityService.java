package com.upc.learntrack.activity.service;

import com.upc.learntrack.activity.dto.LearningActivityDto;
import java.util.List;

public interface LearningActivityService {
   List<LearningActivityDto> findAll();
   LearningActivityDto findById(Long id);
   LearningActivityDto save(Long topicId, LearningActivityDto dto, String userEmail);
   LearningActivityDto update(Long id, LearningActivityDto dto, String userEmail);
   void delete(Long id, String userEmail);
   LearningActivityDto findByIdForStudent(Long id, String userEmail);
   List<LearningActivityDto> findPendingActivities(String studentEmail);
   List<LearningActivityDto> findByTopicId(Long topicId);
   List<LearningActivityDto> findMyPersonalResources(String userEmail);
}