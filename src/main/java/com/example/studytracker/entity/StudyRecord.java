package com.example.studytracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "study_records")
public class StudyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 多(StudyRecord) -> 1(Goal)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @NotNull(message = "日付は必須です")
    @Column(name = "recorded_at", nullable = false)
    private LocalDate recordedAt;

    @NotBlank(message = "内容は必須です")
    @Size(max = 100, message = "内容は100文字以内にしてください")
    @Column(name = "content", nullable = false, length = 100)
    private String content;

    @NotNull(message = "学習時間（分）は必須です")
    @Positive(message = "学習時間（分）は正の数にしてください")
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    public StudyRecord() {
    }

    public StudyRecord(Goal goal, LocalDate recordedAt, String content, Integer durationMinutes) {
        this.goal = goal;
        this.recordedAt = recordedAt;
        this.content = content;
        this.durationMinutes = durationMinutes;
    }

    public Long getId() {
        return id;
    }

    public Goal getGoal() {
        return goal;
    }

    public LocalDate getRecordedAt() {
        return recordedAt;
    }

    public String getContent() {
        return content;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public void setRecordedAt(LocalDate recordedAt) {
        this.recordedAt = recordedAt;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
