package com.example.bc_praca_x;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.bc_praca_x.custom_dialog.CustomDialog;
import com.example.bc_praca_x.database.viewmodel.ActivityViewModel;
import com.example.bc_praca_x.databinding.ActivityMainBinding;
import com.google.android.material.card.MaterialCardView;


public class ProfileFragment extends FragmentSetup {

    private ActivityViewModel activityViewModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if (floatingButtonController != null) {
            floatingButtonController.updateFloatingButton(
                    null,
                    null,
                    null,
                    false,
                    0);
        }

        if (getView() != null) {
            Context context = requireContext();
            UserSettingsManager settings = new UserSettingsManager(context);
            setUI(settings, getView());

            if(settings.getSetting(UserSettingsManager.UPDATED_SETTINGS).equals("true")) {
                restartActivityWithUpdatedTheme();
                settings.saveSetting(UserSettingsManager.UPDATED_SETTINGS, "false");
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        activityViewModel = new ActivityViewModel(this.requireActivity().getApplication());

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchUserSettingsAndSetUI(view);
    }

    private void fetchUserSettingsAndSetUI(View view) {
        Context context = requireContext();
        UserSettingsManager settings = new UserSettingsManager(context);
        setUI(settings, view);
    }

    private void setUI(UserSettingsManager settings, View view) {
        // VIEW INITIALIZATION
        TextView username_btn = view.findViewById(R.id.user_name);
        TextView theme_btn = view.findViewById(R.id.theme_btn);
        TextView language_btn = view.findViewById(R.id.language_btn);
        TextView registration_date = view.findViewById(R.id.register_text);
        TextView total_learning_time = view.findViewById(R.id.total_learning_time);
        TextView imported_flashcards = view.findViewById(R.id.importedFlashcards);
        TextView pomodoro_time = view.findViewById(R.id.pomodoro_btn);
        LinearLayout app_tutorial_btn = view.findViewById(R.id.app_tutorial_btn);
        LinearLayout reset_app_btn = view.findViewById(R.id.reset_app_btn);
        LinearLayout delete_flashcard_btn = view.findViewById(R.id.delete_flashcardsBtn);

        // TEXT SETTING
        username_btn.setText(settings.getSetting("username"));
        language_btn.setText(settings.getSetting("app_language"));
        imported_flashcards.setText(settings.getSetting(UserSettingsManager.IMPORTED_FLASHCARDS));
        pomodoro_time.setText(settings.getSetting("pomodoro_time"));
        registration_date.setText(settings.getSetting(UserSettingsManager.REGISTRATION_DATE));
        theme_btn.setText(getString(R.string.theme_label, settings.getSetting("app_theme")));
        activityViewModel.getTotalTimeSpentFormatted().observe(getViewLifecycleOwner(), total_learning_time::setText);

        // BUTTON LISTENERS
        MaterialCardView user_settings = view.findViewById(R.id.user_settings_holder);
        user_settings.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), LoginActivity.class));
        });

        delete_flashcard_btn.setOnClickListener(v -> removeFlashcards());

        app_tutorial_btn.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), TutorialActivity.class));
        });

        reset_app_btn.setOnClickListener(v -> resetApp());
    }

    private void restartActivityWithUpdatedTheme() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            ActivityMainBinding mainBinding = ((MainActivity) requireActivity()).getBinding();
            mainBinding.bottomNavigationView.setSelectedItemId(R.id.home_menu);

            requireActivity().recreate();
        }, 10);

    }

    private void resetApp(){
        CustomDialog dialog = CustomDialog.deleteDialogInstance("resetApp", -1, null);
        dialog.show(getChildFragmentManager(), "custom_dialog");
    }

    private void removeFlashcards() {
        CustomDialog dialog = CustomDialog.deleteDialogInstance("deleteFlashcards", -1, null);
        dialog.show(getChildFragmentManager(), "custom_dialog");
    }
}