package com.example.studytracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Entity
@Table(name = "goals") // テーブル名を明示
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主キー

    @Column(name = "user_id", nullable = false)
    private Long userId; // 将来的にUserエンティティと紐づける（外部キー予定）

    @NotBlank(message = "タイトルは必須です")
    @Size(max = 100, message = "タイトルは100文字以内にしてください")
    @Column(name = "title", nullable = false, length = 100)
    private String title; // 目標タイトル

    @NotNull(message = "達成予定日は必須です")
    @FutureOrPresent(message = "達成日は今日以降にしてください")
    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Size(max = 150, message = "説明は150文字以内にしてください")
    @Column(name = "description", length = 150)
    private String description; // 詳細説明

    @NotBlank(message = "ステータスは必須です")
    @Column(name = "status", nullable = false)
    private String status; // 状態（Not Started, Active, Completed）

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 登録日時

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新日時

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("recordedAt DESC")
    private List<StudyRecord> studyRecords = new ArrayList<>();

    // --- コンストラクタ ---
    public Goal() {
    }

    public Goal(Long userId, String title, LocalDate targetDate, String description, String status) {
        this.userId = userId;
        this.title = title;
        this.targetDate = targetDate;
        this.description = description;
        this.status = status;
        // this.createdAt = LocalDateTime.now();
    }

    // --- ゲッター・セッター ---
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // --- 更新用メソッド（便利） ---
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
}
