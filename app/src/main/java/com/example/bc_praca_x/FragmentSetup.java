package com.example.bc_praca_x;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.example.bc_praca_x.helpers.LocaleHelper;


public class FragmentSetup extends Fragment {
    protected FloatingButtonController floatingButtonController;
    protected UserSettingsManager settings;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        settings = new UserSettingsManager(context);
        setLanguage(context);

        if (context instanceof FloatingButtonController) {
            floatingButtonController = (FloatingButtonController) context;
        } else {
            floatingButtonController = null;
            //throw new RuntimeException(context.toString() + " must implement FloatingButtonController");
        }
    }

    private void setLanguage(Context context) {

        String language = settings.getSetting("app_language");
        if (language.equals("SLOVAK")) {
            language = "sk";
        } else {
            language = "en-EN";
        }

        LocaleHelper.setLocale(context, language);
    }
}