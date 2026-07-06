package com.upc.learntrack.assessment.repository;

import com.upc.learntrack.assessment.model.StatisticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StatisticsSnapshotRepository extends JpaRepository<StatisticsSnapshot, Long> {
    List<StatisticsSnapshot> findAllByGroup_Code(String groupCode);
}