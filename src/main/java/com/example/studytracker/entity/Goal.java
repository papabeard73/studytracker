package com.example.studytracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals") // テーブル名を明示（推奨）
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主キー

    @Column(name = "user_id", nullable = false)
    private Long userId; // 将来的にUserエンティティと紐づける（外部キー予定）

    @Column(name = "title", nullable = false, length = 100)
    private String title; // 目標タイトル

    @Column(name = "description", length = 500)
    private String description; // 詳細説明

    @Column(name = "status", nullable = false)
    private String status; // 状態（Not Started, Active, Completed）

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 登録日時

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新日時

    // --- コンストラクタ ---
    public Goal() {}

    public Goal(Long userId, String title, String description, String status) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // --- ゲッター・セッター ---
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // --- 更新用メソッド（便利） ---
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
