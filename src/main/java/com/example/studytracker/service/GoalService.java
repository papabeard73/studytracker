package com.example.studytracker.service;

import com.example.studytracker.entity.Goal;
import com.example.studytracker.exception.ResourceNotFoundException;
import com.example.studytracker.repository.GoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional(readOnly = true)
public class GoalService {
    // ログ用
    private static final Logger log = LoggerFactory.getLogger(GoalService.class);

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public Goal getGoalOrThrow(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found: " + id));
    }

    // --- Create ---
    @Transactional
    public Goal createGoal(Goal goal) {
        // ログ
        log.info("Creating goal: title={}, userId={}",
                goal.getTitle(), goal.getUserId());

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
    @Transactional
    public Goal updateGoal(Long id, Goal updated) {
        // ログ
        log.info("Updating goal id={}", id);

        return goalRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updated.getTitle());
                    existing.setTargetDate(updated.getTargetDate());
                    existing.setDescription(updated.getDescription());
                    existing.setStatus(updated.getStatus());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return goalRepository.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found: " + id));
    }

    // --- Delete ---
    @Transactional
    public void deleteGoal(Long id) {
        // ログ
        log.info("Deleting goal id={}", id);

        // delete前に存在確認しておくと、404を返しやすい
        getGoalOrThrow(id);
        goalRepository.deleteById(id);
    }
}
