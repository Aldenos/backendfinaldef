package com.upc.learntrack.learningpath.service;

import com.upc.learntrack.learningpath.dto.LearningPathDto;
import com.upc.learntrack.learningpath.dto.StudentLearningPathDto;
import java.util.List;

public interface LearningPathService {
    List<LearningPathDto> findAllByStudentEmail(String email);
    List<LearningPathDto> findAllByStudentId(Long studentId);
    LearningPathDto findById(Long id);
    LearningPathDto getAdaptivePath(String collectionName, String studentEmail);
    LearningPathDto getAdaptivePathForTopic(Long topicId, String studentEmail);  // nuevo
    List<StudentLearningPathDto> findLearningPathsByGroupCode(String groupCode);
}