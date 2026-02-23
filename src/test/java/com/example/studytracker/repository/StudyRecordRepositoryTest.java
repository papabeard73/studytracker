package com.example.studytracker.repository;

import com.example.studytracker.entity.Goal;
import com.example.studytracker.entity.GoalStatus;
import com.example.studytracker.entity.StudyRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StudyRecordRepositoryTest {

    @Autowired
    GoalRepository goalRepository;

    @Autowired
    StudyRecordRepository studyRecordRepository;

    @Test
    void findByGoalIdOrderByRecordedAtDesc_ordersDesc() {
        Goal goal = new Goal();
        goal.setUserId(1L);
        goal.setTitle("T");
        goal.setTargetDate(LocalDate.of(2099, 1, 13));
        goal.setDescription("D");
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setCreatedAt(LocalDateTime.now());
        goal = goalRepository.save(goal);

        StudyRecord r1 = new StudyRecord();
        r1.setGoal(goal);
        r1.setRecordedAt(LocalDate.of(2026, 1, 1));
        r1.setContent("A");
        r1.setDurationMinutes(10);

        StudyRecord r2 = new StudyRecord();
        r2.setGoal(goal);
        r2.setRecordedAt(LocalDate.of(2026, 1, 3));
        r2.setContent("B");
        r2.setDurationMinutes(20);

        studyRecordRepository.save(r1);
        studyRecordRepository.save(r2);

        List<StudyRecord> result = studyRecordRepository.findByGoalIdOrderByRecordedAtDesc(goal.getId());

        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2026, 1, 3), result.get(0).getRecordedAt());
        assertEquals(LocalDate.of(2026, 1, 1), result.get(1).getRecordedAt());
    }
}
