package com.example.studytracker.service;

import com.example.studytracker.entity.Goal;
import com.example.studytracker.entity.StudyRecord;
import com.example.studytracker.exception.ResourceNotFoundException;
import com.example.studytracker.repository.StudyRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void updateRecord_whenExists_updatesFieldsAndSaves() {
        Goal goal = new Goal();
        goal.setId(1L);

        StudyRecord existing = new StudyRecord();
        existing.setId(10L);
        existing.setGoal(goal);
        existing.setRecordedAt(LocalDate.of(2026, 1, 1));
        existing.setContent("before");
        existing.setDurationMinutes(30);

        StudyRecord updated = new StudyRecord();
        updated.setRecordedAt(LocalDate.of(2026, 1, 2));
        updated.setContent("after");
        updated.setDurationMinutes(60);

        when(studyRecordRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(studyRecordRepository.save(any(StudyRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        StudyRecord result = studyRecordService.updateRecord(goal, 10L, updated);

        assertEquals(LocalDate.of(2026, 1, 2), result.getRecordedAt());
        assertEquals("after", result.getContent());
        assertEquals(60, result.getDurationMinutes());
        verify(studyRecordRepository).save(existing);
    }

    @Test
    void updateRecord_whenNotExists_throwsNotFound() {
        Goal goal = new Goal();
        goal.setId(1L);
        StudyRecord updated = new StudyRecord();

        when(studyRecordRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studyRecordService.updateRecord(goal, 99L, updated));
        verify(studyRecordRepository, never()).save(any());
    }

    @Test
    void deleteRecord_whenExists_deletes() {
        Goal goal = new Goal();
        goal.setId(1L);

        StudyRecord existing = new StudyRecord();
        existing.setId(11L);
        existing.setGoal(goal);

        when(studyRecordRepository.findById(11L)).thenReturn(Optional.of(existing));

        studyRecordService.deleteRecord(goal, 11L);

        verify(studyRecordRepository).delete(existing);
    }

    @Test
    void deleteRecord_whenNotExists_throwsNotFound() {
        Goal goal = new Goal();
        goal.setId(1L);

        when(studyRecordRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studyRecordService.deleteRecord(goal, 100L));
        verify(studyRecordRepository, never()).delete(any());
    }
}
