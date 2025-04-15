package com.example.bc_praca_x.database.enums;

public enum PomodoroTime {
    OFF(0),
    M20(20),
    M25(25),
    M30(30),
    M35(35),
    M40(40),
    M45(45);


    private final int minutes;

    PomodoroTime(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }
}
