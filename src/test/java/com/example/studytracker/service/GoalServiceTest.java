package com.example.studytracker.service;

import com.example.studytracker.entity.Goal;
import com.example.studytracker.exception.ResourceNotFoundException;
import com.example.studytracker.repository.GoalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    GoalRepository goalRepository;

    @InjectMocks
    GoalService goalService;

    @Test
    void getAllGoals_returnsRepositoryResult() {
        Goal g1 = new Goal();
        Goal g2 = new Goal();
        when(goalRepository.findAll()).thenReturn(List.of(g1, g2));

        List<Goal> result = goalService.getAllGoals();

        assertEquals(2, result.size());
        verify(goalRepository, times(1)).findAll();
    }

    @Test
    void getGoalOrThrow_whenExists_returnsGoal() {
        Goal goal = new Goal();
        goal.setId(1L);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        Goal result = goalService.getGoalOrThrow(1L);

        assertEquals(1L, result.getId());
        verify(goalRepository).findById(1L);
    }

    @Test
    void getGoalOrThrow_whenNotExists_throwsNotFound() {
        when(goalRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> goalService.getGoalOrThrow(999L));
        verify(goalRepository).findById(999L);
    }

    @Test
    void createGoal_callsSave() {
        Goal input = new Goal();
        when(goalRepository.save(input)).thenReturn(input);

        Goal result = goalService.createGoal(input);

        assertSame(input, result);
        verify(goalRepository).save(input);
    }

    @Test
    void updateGoal_whenExists_updatesFieldsAndSaves() {
        Goal existing = new Goal();
        existing.setId(1L);
        existing.setTitle("old");
        existing.setDescription("old desc");
        existing.setStatus("Not Started");

        Goal updated = new Goal();
        updated.setTitle("new");
        updated.setDescription("new desc");
        updated.setStatus("Active");

        when(goalRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(goalRepository.save(any(Goal.class))).thenAnswer(inv -> inv.getArgument(0));

        Goal result = goalService.updateGoal(1L, updated);

        assertEquals("new", result.getTitle());
        assertEquals("new desc", result.getDescription());
        assertEquals("Active", result.getStatus());

        // saveされた中身も確認（必要なら）
        ArgumentCaptor<Goal> captor = ArgumentCaptor.forClass(Goal.class);
        verify(goalRepository).save(captor.capture());
        assertEquals("new", captor.getValue().getTitle());
    }

    @Test
    void updateGoal_whenNotExists_throwsNotFound() {
        Goal updated = new Goal();
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> goalService.updateGoal(1L, updated));
        verify(goalRepository).findById(1L);
        verify(goalRepository, never()).save(any());
    }

    @Test
    void deleteGoal_whenExists_deletes() {
        Goal existing = new Goal();
        existing.setId(1L);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(existing));

        goalService.deleteGoal(1L);

        verify(goalRepository).findById(1L);
        verify(goalRepository).deleteById(1L);
    }

    @Test
    void deleteGoal_whenNotExists_throwsNotFound() {
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> goalService.deleteGoal(1L));
        verify(goalRepository).findById(1L);
        verify(goalRepository, never()).deleteById(anyLong());
    }
}
