package com.upc.learntrack.assessment.service;

import com.upc.learntrack.assessment.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface ActivityResultService {
   List<ActivityResultDto> findAll();
   ActivityResultDto findById(Long id);
   List<ActivityResultDto> findAllMyResults(String studentEmail, String type, Long topicId, String sort);
   ActivityResultDetailedResponseDto submit(String topicName, String activityTitle, ActivityResultSubmitDto dto, String studentEmail);
   ActivityResultDetailDto findResultDetail(Long resultId, String userEmail);
   List<TimelinePointDto> getTimeline(String studentEmail, Long topicId, LocalDate startDate, LocalDate endDate);
}