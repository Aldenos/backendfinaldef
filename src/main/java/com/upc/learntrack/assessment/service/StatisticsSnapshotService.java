package com.upc.learntrack.assessment.service;

import com.upc.learntrack.assessment.dto.StatisticsSnapshotDto;
import java.util.List;

public interface StatisticsSnapshotService {
    List<StatisticsSnapshotDto> findAllByGroupCode(String groupCode);
    StatisticsSnapshotDto save(StatisticsSnapshotDto dto);
}