package com.example.studytracker.service;

import com.example.studytracker.entity.Goal;
import com.example.studytracker.entity.StudyRecord;
import com.example.studytracker.repository.StudyRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyRecordService {

    private final StudyRecordRepository studyRecordRepository;

    public StudyRecordService(StudyRecordRepository studyRecordRepository) {
        this.studyRecordRepository = studyRecordRepository;
    }

    public List<StudyRecord> getRecordsByGoalId(Long goalId) {
        return studyRecordRepository.findByGoalIdOrderByRecordedAtDesc(goalId);
    }

    public StudyRecord addRecord(Goal goal, StudyRecord record) {
        record.setGoal(goal);
        return studyRecordRepository.save(record);
    }
}
