package com.upc.learntrack.activity.service.impl;

import com.upc.learntrack.activity.dto.AssignmentDto;
import com.upc.learntrack.activity.exception.AssignmentNotFoundException;
import com.upc.learntrack.activity.exception.LearningActivityNotFoundException;
import com.upc.learntrack.activity.mapper.AssignmentMapper;
import com.upc.learntrack.activity.model.Assignment;
import com.upc.learntrack.activity.model.LearningActivity;
import com.upc.learntrack.activity.repository.ActivityRepository;
import com.upc.learntrack.activity.repository.AssignmentRepository;
import com.upc.learntrack.activity.service.AssignmentService;
import com.upc.learntrack.course.exception.GroupNotFoundException;
import com.upc.learntrack.course.model.Group;
import com.upc.learntrack.course.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final ActivityRepository activityRepository;
    private final GroupRepository groupRepository;
    private final AssignmentMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentDto> findAll() {
        return assignmentRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentDto findById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new AssignmentNotFoundException("Asignación no encontrada con id: " + id));
        return mapper.toDto(assignment);
    }

    @Override
    @Transactional
    public List<AssignmentDto> assignToGroups(Long activityId, AssignmentDto dto) {
        LearningActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new LearningActivityNotFoundException("Actividad no encontrada"));

        List<AssignmentDto> createdAssignments = new ArrayList<>();
        for (String groupCode : dto.getGroupCodes()) {
            Group group = groupRepository.findByCode(groupCode)
                    .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado con código: " + groupCode));

            if (assignmentRepository.existsByActivityIdAndGroupId(activityId, group.getId())) {
                throw new IllegalArgumentException("La actividad ya está asignada al grupo " + groupCode);
            }

            Assignment assignment = new Assignment();
            assignment.setActivity(activity);
            assignment.setGroup(group);
            assignment.setDueDate(dto.getDueDate());

            Assignment saved = assignmentRepository.save(assignment);
            AssignmentDto response = mapper.toDto(saved);
            response.setActivityTitle(activity.getTitle());
            response.setGroupCodes(Collections.singletonList(groupCode));
            createdAssignments.add(response);
        }
        return createdAssignments;
    }

    @Override
    @Transactional
    public AssignmentDto update(Long id, AssignmentDto dto) {
        Assignment existing = assignmentRepository.findById(id)
                .orElseThrow(() -> new AssignmentNotFoundException("Asignación no encontrada con id: " + id));
        existing.setDueDate(dto.getDueDate());
        return mapper.toDto(assignmentRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Assignment existing = assignmentRepository.findById(id)
                .orElseThrow(() -> new AssignmentNotFoundException("Asignación no encontrada con id: " + id));
        assignmentRepository.delete(existing);
    }
}