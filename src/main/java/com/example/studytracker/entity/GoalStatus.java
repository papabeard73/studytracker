package com.example.studytracker.entity;

public enum GoalStatus {
    NOT_STARTED("Not Started"),
    ACTIVE("Active"),
    COMPLETED("Completed");

    private final String label;

    GoalStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
