package com.example.bc_praca_x;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.bc_praca_x.custom_dialog.CustomDialog;
import com.example.bc_praca_x.helpers.LocaleHelper;
import com.example.bc_praca_x.helpers.PomodoroTimer;


public class ActivitySetup extends AppCompatActivity {
    private UserSettingsManager settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (settings.getSetting("first_run").equals("true")) {
            LocaleHelper.setLocale(this, "en-EN"); //default language
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        applyTheme();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Configuration config = newBase.getResources().getConfiguration();
        if (config.fontScale != 1.0f) {
            config.fontScale = 1.0f;
            newBase = newBase.createConfigurationContext(config);
        }

        super.attachBaseContext(newBase);

        settings = new UserSettingsManager(newBase);
        setLanguage();
    }


    private void applyTheme() {
        switch (settings.getSetting("app_theme")) {
            case "GREEN":
                setTheme(R.style.green);
                break;
            case "BLUE":
                setTheme(R.style.blue);
                break;
            default:
                setTheme(R.style.orange);
        }
    }

    private void setLanguage() {
        String language = settings.getSetting("app_language");
        if (language.equals("SLOVAK")) {
            language = "sk";
        } else {
            language = "en-EN";
        }

        LocaleHelper.setLocale(this, language);
    }

    private final BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (PomodoroTimer.TIMER_DONE_ACTION.equals(intent.getAction())) {
                showTimerDoneDialog();
            }
        }
    };

    private void showTimerDoneDialog() {
        CustomDialog dialog = CustomDialog.pomodoroDialogInstance("pomodoro");
        dialog.show(getSupportFragmentManager(), "custom_dialog");
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(PomodoroTimer.TIMER_DONE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(timerReceiver, filter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(timerReceiver);
    }
}
