package com.example.bc_praca_x;

import android.app.Application;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.bc_praca_x.helpers.DeleteUnusedMedia;
import com.example.bc_praca_x.helpers.PomodoroMapper;
import com.example.bc_praca_x.helpers.PomodoroTimer;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {

    private static int activityCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        scheduleMediaCleanup(this);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activityCount == 0) {
            Log.d("PomodoroTimer", "App is foreground");

            UserSettingsManager settings = new UserSettingsManager(activity);
            int minutes = PomodoroMapper.getPomodoroTimeInMinutes(settings);
            Log.d("PomodoroTimer", "Starting timer for " + minutes + " minutes");

            if (!PomodoroTimer.getInstance().isTimerRunning() && minutes > 0) {
                PomodoroTimer.getInstance().startTimer(activity, (long) minutes * 60 * 1000);
            }
        }
        activityCount++;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityCount--;
        if (activityCount == 0) {
            Log.d("PomodoroTimer", "App went to the background");
            PomodoroTimer.getInstance().stopTimer();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
    @Override
    public void onActivityResumed(Activity activity) {}
    @Override
    public void onActivityPaused(Activity activity) {}
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
    @Override
    public void onActivityDestroyed(Activity activity) {}

    public void scheduleMediaCleanup(Context context) {
        long delay = getDelayUntilMidnight();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(DeleteUnusedMedia.class, 1, TimeUnit.DAYS).setInitialDelay(delay, TimeUnit.MILLISECONDS).build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("mnemos_cleanup_worker", ExistingPeriodicWorkPolicy.KEEP, workRequest);
    }

    private long getDelayUntilMidnight() {
        Calendar now = Calendar.getInstance();
        Calendar midnight = (Calendar) now.clone();
        midnight.set(Calendar.HOUR_OF_DAY, 23);
        midnight.set(Calendar.MINUTE, 59);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        if (now.after(midnight)) midnight.add(Calendar.DAY_OF_MONTH, 1);

        return midnight.getTimeInMillis() - now.getTimeInMillis();
    }

}
