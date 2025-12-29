package com.example.studytracker.config;

import com.example.studytracker.entity.Goal;
import com.example.studytracker.repository.GoalRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final GoalRepository goalRepository;

    public DataLoader(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @Override
    public void run(String... args) {
        // user_id は nullable=false なので必ず入れる
        goalRepository.save(new Goal(1L, "Spring Boot CRUD", "Goal CRUDを完成させる", "Active"));
        goalRepository.save(new Goal(1L, "英語学習", "英検1級に向けて単語とリスニング", "Not Started"));
    }
}
