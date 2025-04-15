package com.example.bc_praca_x;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bc_praca_x.database.enums.AppLanguage;
import com.example.bc_praca_x.database.enums.AppTheme;
import com.example.bc_praca_x.database.enums.PomodoroTime;
import com.example.bc_praca_x.helpers.LocaleHelper;
import com.example.bc_praca_x.helpers.PomodoroMapper;
import com.example.bc_praca_x.helpers.PomodoroTimer;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    private UserSettingsManager settings;
    private TextView username;
    private Spinner languageSpinner, themeSpinner, pomodoroSpinner;
    private boolean update;
    private Button loginFinishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        settings = new UserSettingsManager(this);

        update=false;
        username = findViewById(R.id.userName);
        languageSpinner = findViewById(R.id.languageSelectSpinner);
        themeSpinner = findViewById(R.id.appThemeSelectSpinner);
        pomodoroSpinner = findViewById(R.id.pomodoroSelectSpinner);
        loginFinishButton = findViewById(R.id.loginFinishedButton);

        setupSpinners();
        loadUserDataIfUpdate();

        loginFinishButton.setOnClickListener(v -> {
            saveUserData();
        });
    }

    private void saveUserData() {

        if(validate()){
            settings.saveSetting("username", username.getText().toString());
            settings.saveSetting("app_language", languageSpinner.getSelectedItem().toString());
            settings.saveSetting("app_theme", themeSpinner.getSelectedItem().toString());
            settings.saveSetting("pomodoro_time", pomodoroSpinner.getSelectedItem().toString());

            Log.d("PomodoroTimer", "Saving settings: " + settings.getSetting("pomodoro_time"));

            int minutes = PomodoroMapper.getPomodoroTimeInMinutes(settings);
            if(minutes>0) PomodoroTimer.getInstance().startTimer(this, (long) minutes * 60 * 1000);

            if(!update) {
                settings.saveSetting("first_run", "false");
                settings.saveSetting(UserSettingsManager.CATEGORY_FILTER_TYPE, "filter_type_all");
                settings.saveSetting(UserSettingsManager.CATEGORY_SORT_TYPE, "filter_sort_newest");
                settings.saveSetting(UserSettingsManager.PACKAGE_FILTER_TYPE, "filter_type_all");
                settings.saveSetting(UserSettingsManager.PACKAGE_SORT_TYPE, "filter_sort_newest");
                Date currentDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                settings.saveSetting(UserSettingsManager.REGISTRATION_DATE, dateFormat.format(currentDate));
                startActivity(new Intent(this, TutorialActivity.class));
            } else{
                Toast.makeText(this, getString(R.string.settings_updated_toas), Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }

    private boolean validate(){
        if(username.getText().toString().isEmpty()){
            username.setError(getString(R.string.required));
            username.requestFocus();
            return false;
        }

        return true;
    }

    private void setupSpinners(){
        ArrayList<String> languages = new ArrayList<>();
        for (AppLanguage language : AppLanguage.values()) {
            languages.add(language.toString());
        }

        ArrayList<String> themes = new ArrayList<>();
        for (AppTheme theme : AppTheme.values()) {
            themes.add(theme.toString());
        }

        ArrayList<String> pomodoros = new ArrayList<>();
        for (PomodoroTime pt : PomodoroTime.values()) {
            if (pt.getMinutes() == 0) {
                pomodoros.add("OFF");
            } else {
                pomodoros.add(pt.getMinutes() + " m");
            }
        }

        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);

        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, themes);
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeSpinner.setAdapter(themeAdapter);

        ArrayAdapter<String> pomodoroAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pomodoros);
        pomodoroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pomodoroSpinner.setAdapter(pomodoroAdapter);
    }

    private void loadUserDataIfUpdate(){

        if(settings.getSetting("first_run").equals("false")){
            TextView welcomeText = findViewById(R.id.welcome_text);
            welcomeText.setText(getString(R.string.welcome_text_update));

            loginFinishButton.setText(getString(R.string.Update));
            update = true;
            username.setText(settings.getSetting("username"));

            languageSpinner.setSelection(((ArrayAdapter<String>)languageSpinner.getAdapter()).getPosition(settings.getSetting("app_language")));
            themeSpinner.setSelection(((ArrayAdapter<String>)themeSpinner.getAdapter()).getPosition(settings.getSetting("app_theme")));
            pomodoroSpinner.setSelection(((ArrayAdapter<String>)pomodoroSpinner.getAdapter()).getPosition(settings.getSetting("pomodoro_time")));

            settings.saveSetting("updated_settings", "true");
        } else{
            LocaleHelper.setLocale(this, "en-EN");
        }

    }
}