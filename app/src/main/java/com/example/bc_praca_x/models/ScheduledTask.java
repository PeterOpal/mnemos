package com.example.bc_praca_x.models;

import java.util.List;

public class ScheduledTask {
    String taskName; // pack name or user task name
    long taskId;
    boolean userTask;
    long packID;
    String cardsToReview;
    boolean isCompleted;
    boolean isOverdue;
    Boolean isSectionHeader;
    List<ScheduledTask> subTasks;
    public String displayDate;

    public ScheduledTask(String taskName, long taskId, long packId, String description, boolean completed, boolean isSectionHeader, String displayDate, List<ScheduledTask> subTasks, boolean userTask) {
        this.taskName = taskName;
        this.taskId = taskId;
        this.packID = packId;
        this.cardsToReview = description;
        this.isCompleted = completed;
        this.isSectionHeader = isSectionHeader;
        this.displayDate = displayDate;
        this.subTasks = subTasks;
        this.userTask = userTask;
    }
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public long getPackID() {
        return packID;
    }

    public void setPackID(long packID) {
        this.packID = packID;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getCardsToReview() {
        return cardsToReview;
    }

    public void setCardsToReview(String cardsToReview) {
        this.cardsToReview = cardsToReview;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isOverdue() {
        return isOverdue;
    }

    public void setOverdue(boolean overdue) {
        isOverdue = overdue;
    }

    public Boolean isSectionHeader() {
        return isSectionHeader;
    }

    public void setSectionHeader(Boolean sectionHeader) {
        isSectionHeader = sectionHeader;
    }

    public boolean isUserTask() {
        return userTask;
    }

    public List<ScheduledTask> getSubTasks() {
        return subTasks;
    }
}
