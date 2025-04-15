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
        tableName = "cards",
        foreignKeys = @ForeignKey(
                entity = CardPack.class,
                parentColumns = "id",
                childColumns = "cardPackId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "cardPackId")}
)
@TypeConverters(DateConverter.class)
public class Card {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public int timeSpentInMinutes;
    public double easeFactor;
    public int repetitionsCount;
    public Date nextReviewDate;
    public Date lastReviewDate;
    public Date createdAt;
    public long cardPackId;
    public int SMinterval;

    public Card(long id, int timeSpentInMinutes, double easeFactor, int repetitionsCount, Date nextReviewDate, Date lastReviewDate, Date createdAt, long cardPackId, int SMinterval) {
        this.id = id;
        this.timeSpentInMinutes = timeSpentInMinutes;
        this.easeFactor = easeFactor;
        this.repetitionsCount = repetitionsCount;
        this.nextReviewDate = nextReviewDate;
        this.lastReviewDate = lastReviewDate;
        this.createdAt = createdAt;
        this.cardPackId = cardPackId;
        this.SMinterval = SMinterval;
    }

    @Ignore
    public Card(int timeSpentInMinutes, double easeFactor, int repetitionsCount, Date nextReviewDate, Date lastReviewDate, Date createdAt, long cardPackId, int interval) {
        this.timeSpentInMinutes = timeSpentInMinutes;
        this.easeFactor = easeFactor;
        this.repetitionsCount = repetitionsCount;
        this.nextReviewDate = nextReviewDate;
        this.lastReviewDate = lastReviewDate;
        this.createdAt = createdAt;
        this.cardPackId = cardPackId;
        this.SMinterval = interval;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTimeSpentInMinutes() {
        return timeSpentInMinutes;
    }

    public void setTimeSpentInMinutes(int timeSpentInMinutes) {
        this.timeSpentInMinutes = timeSpentInMinutes;
    }

    public double getEaseFactor() {
        return easeFactor;
    }

    public void setEaseFactor(double easeFactor) {
        this.easeFactor = easeFactor;
    }

    public int getRepetitionsCount() {
        return repetitionsCount;
    }

    public void setRepetitionsCount(int repetitionsCount) {
        this.repetitionsCount = repetitionsCount;
    }

    public Date getNextReviewDate() {
        return nextReviewDate;
    }

    public void setNextReviewDate(Date nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }

    public Date getLastReviewDate() {
        return lastReviewDate;
    }

    public void setLastReviewDate(Date lastReviewDate) {
        this.lastReviewDate = lastReviewDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getCardPackId() {
        return cardPackId;
    }

    public void setCardPackId(long cardPackId) {
        this.cardPackId = cardPackId;
    }

    public int getSMinterval() {return SMinterval;}

    public void setSMinterval(int SMinterval) {this.SMinterval = SMinterval;}
}
