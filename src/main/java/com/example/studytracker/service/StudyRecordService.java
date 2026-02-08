package com.example.studytracker.service;

import com.example.studytracker.entity.Goal;
import com.example.studytracker.entity.StudyRecord;
import com.example.studytracker.repository.StudyRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class StudyRecordService {
    // ログ用
    private static final Logger log = LoggerFactory.getLogger(StudyRecordService.class);

    private final StudyRecordRepository studyRecordRepository;

    public StudyRecordService(StudyRecordRepository studyRecordRepository) {
        this.studyRecordRepository = studyRecordRepository;
    }

    public List<StudyRecord> getRecordsByGoalId(Long goalId) {
        return studyRecordRepository.findByGoalIdOrderByRecordedAtDesc(goalId);
    }

    public StudyRecord addRecord(Goal goal, StudyRecord record) {
        // ログ
        log.info("Adding study record: goalId={}, date={}, minutes={}",
                goal.getId(),
                record.getRecordedAt(),
                record.getDurationMinutes());

        record.setGoal(goal);
        return studyRecordRepository.save(record);
    }

    public StudyRecord getRecordOrThrow(Long recordId) {
        return studyRecordRepository.findById(recordId)
                .orElseThrow(() -> new com.example.studytracker.exception.ResourceNotFoundException(
                        "StudyRecord not found: " + recordId));
    }

    public StudyRecord updateRecord(Goal goal, Long recordId, StudyRecord updated) {
        log.info("Updating study record id={}", recordId);

        StudyRecord existing = getRecordOrThrow(recordId);
        if (!Objects.equals(existing.getGoal().getId(), goal.getId())) {
            throw new com.example.studytracker.exception.ResourceNotFoundException(
                    "StudyRecord not found for goal: " + recordId);
        }

        existing.setRecordedAt(updated.getRecordedAt());
        existing.setContent(updated.getContent());
        existing.setDurationMinutes(updated.getDurationMinutes());
        return studyRecordRepository.save(existing);
    }
}
