package com.upc.learntrack.learningpath.service;

import com.upc.learntrack.learningpath.dto.ConceptualGapDto;
import com.upc.learntrack.learningpath.dto.GroupGapDto;
import com.upc.learntrack.learningpath.dto.SubTopicGapDto;
import java.util.List;

public interface ConceptualGapService {
    List<ConceptualGapDto> findAllByLearningPathId(Long learningPathId);
    void resolveGap(Long id);
    List<GroupGapDto> findGroupGaps(String groupCode);
    List<SubTopicGapDto> findSubTopicGapsByGroup(String groupCode);
    List<SubTopicGapDto> findSubTopicGapsByLearningPath(Long learningPathId);
}