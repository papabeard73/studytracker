package com.example.studytracker.service;

import com.example.studytracker.entity.Goal;
import com.example.studytracker.repository.GoalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GoalService {

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    // --- Create ---
    public Goal createGoal(Goal goal) {
        goal.setCreatedAt(LocalDateTime.now());
        goal.setUpdatedAt(LocalDateTime.now());
        return goalRepository.save(goal);
    }

    // --- Read (All) ---
    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    // --- Read (One) ---
    public Optional<Goal> getGoalById(Long id) {
        return goalRepository.findById(id);
    }

    // --- Update ---
    public Goal updateGoal(Long id, Goal updated) {
        return goalRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updated.getTitle());
                    existing.setDescription(updated.getDescription());
                    existing.setStatus(updated.getStatus());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return goalRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Goal not found: " + id));
    }

    // --- Delete ---
    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }
}
