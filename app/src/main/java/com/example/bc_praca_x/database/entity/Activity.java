package com.example.bc_praca_x.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.bc_praca_x.database.enums.ActivityType;
import com.example.bc_praca_x.database.type_converter.DateConverter;
import com.example.bc_praca_x.database.type_converter.EnumConverter;

import java.util.Date;

@Entity(
        tableName = "activities",
        foreignKeys = @ForeignKey(
                entity = CardPack.class,
                parentColumns = "id",
                childColumns = "cardId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "cardId")}
)
@TypeConverters({EnumConverter.class, DateConverter.class})
public class Activity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    private Date date;
    private int timeSpentInSeconds;
    private String revisedCardsFromAll;
    private ActivityType activityType;
    private int successRate;
    private long cardId; //THIS IS CARD PACK ID TO BE CLEAR, IT IS A TYPO

    public Activity(long id, Date date, int timeSpentInSeconds, String revisedCardsFromAll, ActivityType activityType, int successRate, long cardId) {
        this.id = id;
        this.date = date;
        this.timeSpentInSeconds = timeSpentInSeconds;
        this.revisedCardsFromAll = revisedCardsFromAll;
        this.activityType = activityType;
        this.successRate = successRate;
        this.cardId = cardId;
    }

    @Ignore
    public Activity(Date date, int timeSpentInSeconds, String revisedCardsFromAll, ActivityType activityType, int successRate, long cardId) {
        this.date = date;
        this.timeSpentInSeconds = timeSpentInSeconds;
        this.revisedCardsFromAll = revisedCardsFromAll;
        this.activityType = activityType;
        this.successRate = successRate;
        this.cardId = cardId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTimeSpentInSeconds() {
        return timeSpentInSeconds;
    }

    public void setTimeSpentInSeconds(int timeSpentInSeconds) {
        this.timeSpentInSeconds = timeSpentInSeconds;
    }

    public String getRevisedCardsFromAll() {
        return revisedCardsFromAll;
    }

    public void setRevisedCardsFromAll(String revisedCardsFromAll) {
        this.revisedCardsFromAll = revisedCardsFromAll;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public int getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(int successRate) {
        this.successRate = successRate;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }
}
