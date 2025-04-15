package com.example.bc_praca_x.models;

import com.example.bc_praca_x.database.entity.Card;

public class FreeModeWithRating {
    private Card card;
    private int rating;
    private int timeSpentInSeconds;

    public FreeModeWithRating(Card card, int rating, int timeSpentInSeconds) {
        this.card = card;
        this.rating = rating;
        this.timeSpentInSeconds = timeSpentInSeconds;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getTimeSpentInSeconds() {
        return timeSpentInSeconds;
    }

    public void setTimeSpentInSeconds(int timeSpentInSeconds) {
        this.timeSpentInSeconds = timeSpentInSeconds;
    }
}
