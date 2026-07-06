package com.upc.learntrack.activity.service;

import com.upc.learntrack.activity.dto.AssignmentDto;
import java.util.List;

public interface AssignmentService {
   List<AssignmentDto> findAll();
   AssignmentDto findById(Long id);
   List<AssignmentDto> assignToGroups(Long activityId, AssignmentDto dto);
   AssignmentDto update(Long id, AssignmentDto dto);
   void delete(Long id);
}