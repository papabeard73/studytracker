package com.example.studytracker.repository;

import com.example.studytracker.entity.StudyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {
    List<StudyRecord> findByGoalIdOrderByRecordedAtDesc(Long goalId);
}
