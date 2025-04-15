package com.example.bc_praca_x.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class PomodoroTimer {

    private static PomodoroTimer instance;
    private Handler handler;
    private Runnable runnable;
    private long duration;
    private long remainingTime;
    private boolean isRunning;

    public static final String TIMER_DONE_ACTION = "com.example.bc_praca_x.helpers.TIMER_DONE";

    private PomodoroTimer() {
        handler = new Handler();
        isRunning = false;
    }

    public static synchronized PomodoroTimer getInstance() {
        if (instance == null) {
            instance = new PomodoroTimer();
        }
        return instance;
    }

    public void startTimer(Context context, long durationInMillis) {
        Log.d("PomodoroTimer", "Starting timer for " + durationInMillis + " milliseconds");
        stopTimer();

        this.duration = durationInMillis;
        this.remainingTime = durationInMillis;
        isRunning = true;

        runnable = new Runnable() {
            @Override
            public void run() {
                remainingTime -= 1000;

                if (remainingTime <= 0) {
                    Log.d("PomodoroTimer", "Timer finished");
                    stopTimer();
                    sendTimerDoneBroadcast(context);
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    public void stopTimer() {
        if (isRunning) {
            handler.removeCallbacks(runnable);
            isRunning = false;
        }
    }

    public boolean isTimerRunning() {
        return isRunning;
    }

    public void resetTimer() {
        stopTimer();
        remainingTime = duration;
        Log.d("PomodoroTimer", "Timer reset");
    }

    private void sendTimerDoneBroadcast(Context context) {
        Intent intent = new Intent(TIMER_DONE_ACTION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        Log.d("PomodoroTimer", "Broadcast sent");
    }

}
