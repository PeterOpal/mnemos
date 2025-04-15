package com.example.bc_praca_x;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.bc_praca_x.database.enums.AppLanguage;
import com.example.bc_praca_x.database.enums.AppTheme;

public class UserSettingsManager {
    private static final String PREFS_NAME = "user_settings";
    public static final String USERNAME_KEY = "username";
    private static final String LANGUAGE_KEY = "app_language";
    private static final String THEME_KEY = "app_theme";
    private static final String FIRST_RUN_KEY = "first_run";
    public static final String CATEGORY_FILTER_TYPE = "categories_filter_type";
    public static final String CATEGORY_SORT_TYPE = "categories_sort_type";
    public static final String PACKAGE_FILTER_TYPE = "package_filter_type";
    public static final String PACKAGE_SORT_TYPE = "package_sort_type";
    public static final String IMPORTED_FLASHCARDS = "imported_flashcards";
    public static final String REGISTRATION_DATE = "registration_date";
    public static final String POMODORO_TIME = "pomodoro_time";
    public static final String UPDATED_SETTINGS = "updated_settings";

    private final SharedPreferences sharedPreferences;

    public UserSettingsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveSetting(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getSetting(String key) {
        switch (key) {
            case USERNAME_KEY:
                return sharedPreferences.getString(USERNAME_KEY, "");
            case LANGUAGE_KEY:
                return sharedPreferences.getString(LANGUAGE_KEY, AppLanguage.ENGLISH.toString());
            case THEME_KEY:
                return sharedPreferences.getString(THEME_KEY, AppTheme.ORANGE.toString());
            case FIRST_RUN_KEY:
                return sharedPreferences.getString(FIRST_RUN_KEY, "true");
            case CATEGORY_FILTER_TYPE:
                return sharedPreferences.getString(CATEGORY_FILTER_TYPE, "filter_type_all");
            case CATEGORY_SORT_TYPE:
                return sharedPreferences.getString(CATEGORY_SORT_TYPE, "filter_sort_newest");
            case PACKAGE_FILTER_TYPE:
                return sharedPreferences.getString(PACKAGE_FILTER_TYPE, "filter_type_all");
            case PACKAGE_SORT_TYPE:
                return sharedPreferences.getString(PACKAGE_SORT_TYPE, "filter_sort_newest");
            case IMPORTED_FLASHCARDS:
                return sharedPreferences.getString(IMPORTED_FLASHCARDS, "0");
            case REGISTRATION_DATE:
                return sharedPreferences.getString(REGISTRATION_DATE, "");
            case POMODORO_TIME:
                return sharedPreferences.getString(POMODORO_TIME, "25");
            case UPDATED_SETTINGS:
                return sharedPreferences.getString(UPDATED_SETTINGS, "false");
            default:
                return "";
        }
    }
}
