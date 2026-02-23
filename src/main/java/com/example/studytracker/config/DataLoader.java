package com.example.studytracker.config;

import com.example.studytracker.entity.Goal;
import com.example.studytracker.entity.StudyRecord;
import com.example.studytracker.repository.GoalRepository;
import com.example.studytracker.repository.StudyRecordRepository;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DataLoader implements CommandLineRunner {

    private final GoalRepository goalRepository;
    private final StudyRecordRepository studyRecordRepository;

    public DataLoader(GoalRepository goalRepository, StudyRecordRepository studyRecordRepository) {
        this.goalRepository = goalRepository;
        this.studyRecordRepository = studyRecordRepository;
    }

    @Override
    public void run(String... args) {
        // 既にデータがある場合は投入しない（起動のたびの重複を防ぐ）
        if (goalRepository.count() > 0 || studyRecordRepository.count() > 0) {
            return;
        }

        Goal goal1 = goalRepository.save(
                new Goal(1L, "Spring Boot CRUD", LocalDate.now().plusDays(30), "Goal CRUDを完成させる", "Active"));

        Goal goal2 = goalRepository.save(
                new Goal(1L, "英語学習", LocalDate.now().plusDays(60), "英検1級に向けて単語とリスニング", "Not Started"));

        Goal goal3 = goalRepository.save(
                new Goal(1L, "Go言語学習", LocalDate.now().plusDays(40), "コードリーディング", "Active"));

        studyRecordRepository.save(new StudyRecord(goal1, LocalDate.now().minusDays(2), "Entity設計", 90));
        studyRecordRepository.save(new StudyRecord(goal1, LocalDate.now().minusDays(1), "Controller実装", 120));

        studyRecordRepository.save(new StudyRecord(goal2, LocalDate.now(), "単語学習", 45));

        studyRecordRepository.save(new StudyRecord(goal3, LocalDate.now().minusDays(3), "Goの文法復習", 60));
    }
}
