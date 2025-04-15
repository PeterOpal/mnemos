package com.example.bc_praca_x.helpers;

import com.example.bc_praca_x.UserSettingsManager;

public class PomodoroMapper {

    public static int getPomodoroTimeInMinutes(UserSettingsManager settings){
        String pomodoroTime = settings.getSetting(UserSettingsManager.POMODORO_TIME);

        if(pomodoroTime.equals("OFF")) return 0;

        String minutes = pomodoroTime.replaceAll("[^0-9]", "");
        return Integer.parseInt(minutes);
    }
}
