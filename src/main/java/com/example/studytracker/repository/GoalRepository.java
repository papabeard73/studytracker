package com.example.studytracker.repository;

import com.example.studytracker.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Goalエンティティに対するDBアクセスを担当
@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    // 必要に応じてカスタムクエリを追加できる
    // 例）List<Goal> findByStatus(String status);
}
