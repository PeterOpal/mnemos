package com.example.bc_praca_x.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.bc_praca_x.database.type_converter.DateConverter;

import java.util.Date;

@Entity(
        tableName = "tasks",
        foreignKeys = @ForeignKey(
                entity = CardPack.class,
                parentColumns = "id",
                childColumns = "card_packs_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "card_packs_id")}
)
@TypeConverters(DateConverter.class)
public class Task {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String taskName;
    public Date reviewDate;
    public int cardsReviewCount;
    public boolean completed;
    public long card_packs_id;

    public Task(long id, String taskName, Date reviewDate, int cardsReviewCount, boolean completed, long card_packs_id) {
        this.id = id;
        this.taskName = taskName;
        this.reviewDate = reviewDate;
        this.cardsReviewCount = cardsReviewCount;
        this.completed = completed;
        this.card_packs_id = card_packs_id;
    }

    @Ignore
    public Task(String name, Date reviewDate, int cardsReviewCount, boolean completed, long card_packs_id) {
        this.taskName = name;
        this.reviewDate = reviewDate;
        this.cardsReviewCount = cardsReviewCount;
        this.completed = completed;
        this.card_packs_id = card_packs_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setName(String name) {
        this.taskName = name;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public int getCardsReviewCount() {
        return cardsReviewCount;
    }

    public void setCardsReviewCount(int cardsReviewCount) {
        this.cardsReviewCount = cardsReviewCount;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getCard_packs_id() {
        return card_packs_id;
    }

    public void setCard_packs_id(long card_packs_id) {
        this.card_packs_id = card_packs_id;
    }
}
