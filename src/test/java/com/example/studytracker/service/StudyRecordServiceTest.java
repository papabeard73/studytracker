package com.example.studytracker.service;

import com.example.studytracker.entity.Goal;
import com.example.studytracker.entity.StudyRecord;
import com.example.studytracker.repository.StudyRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudyRecordServiceTest {

    @Mock
    StudyRecordRepository studyRecordRepository;

    @InjectMocks
    StudyRecordService studyRecordService;

    @Test
    void getRecordsByGoalId_callsRepositoryMethod() {
        StudyRecord r1 = new StudyRecord();
        StudyRecord r2 = new StudyRecord();
        when(studyRecordRepository.findByGoalIdOrderByRecordedAtDesc(1L)).thenReturn(List.of(r1, r2));

        List<StudyRecord> result = studyRecordService.getRecordsByGoalId(1L);

        assertEquals(2, result.size());
        verify(studyRecordRepository).findByGoalIdOrderByRecordedAtDesc(1L);
    }

    @Test
    void addRecord_setsGoalAndSaves() {
        Goal goal = new Goal();
        goal.setId(1L);

        StudyRecord record = new StudyRecord();
        record.setRecordedAt(LocalDate.of(2026, 1, 1));
        record.setContent("Study");
        record.setDurationMinutes(30);

        when(studyRecordRepository.save(any(StudyRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        StudyRecord saved = studyRecordService.addRecord(goal, record);

        assertNotNull(saved.getGoal());
        assertEquals(1L, saved.getGoal().getId());
        verify(studyRecordRepository).save(record);
    }
}
