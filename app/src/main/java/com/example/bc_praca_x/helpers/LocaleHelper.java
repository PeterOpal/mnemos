package com.example.bc_praca_x.helpers;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleHelper {

    public static void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        resources.updateConfiguration(config, resources.getDisplayMetrics());
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

}
