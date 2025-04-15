package com.example.bc_praca_x.custom_dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.WindowManager;

import com.example.bc_praca_x.FloatingButtonController;
import com.example.bc_praca_x.R;

import eightbitlab.com.blurview.BlurView;

public class CustomDialog extends DialogFragment {
    private static final String ARG_FRAGMENT_TAG = "fragment_tag";
    private static final String ARG_NAME = "name";
    private static final String ARG_ID = "id";
    private static final String ARG_CATEGORY_NAME = "category_name";
    private static final String ARG_TYPE = "type";
    private static final String ARG_FILTER_TAG = "filter_tag";
    private static final String ARG_SORT_TAG = "sort_tag";
    private static final String ARG_DAY = "day";

    //DELETE DIALOG
    public static CustomDialog deleteDialogInstance(String fragmentTag, long category_id, String categoryName) {
        CustomDialog fragment = new CustomDialog();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_TAG, fragmentTag);
        args.putLong(ARG_ID, category_id);
        args.putString(ARG_NAME, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    //PACKAGEOVERVIEW - START LEARNING SESSION
    public static CustomDialog newInstance(String fragmentTag, long packId, String packageName, String categoryName) {
        CustomDialog fragment = new CustomDialog();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_TAG, fragmentTag);
        args.putLong(ARG_ID, packId);
        args.putString(ARG_NAME, packageName);
        args.putString(ARG_CATEGORY_NAME, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    //CARDPRESENTER - PAUSE LEARNING SESSION
    public static CustomDialog pauseDialogInstance(String fragmentTag) {
        CustomDialog fragment = new CustomDialog();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_TAG, fragmentTag);
        fragment.setArguments(args);
        return fragment;
    }

    //FILTER DIALOG
    public static CustomDialog filterDialogInstance(String fragmentTag, String type, String filterTag, String sortTag) {
        CustomDialog fragment = new CustomDialog();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_TAG, fragmentTag);
        args.putString(ARG_TYPE, type);
        args.putString(ARG_FILTER_TAG, filterTag);
        args.putString(ARG_SORT_TAG, sortTag);
        fragment.setArguments(args);
        return fragment;
    }

    //IMPORT - LOADING SPINNER
    public static CustomDialog loadingSpinnerInstance(String fragmentTag) {
        CustomDialog fragment = new CustomDialog();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_TAG, fragmentTag);
        fragment.setArguments(args);
        return fragment;
    }

    //IMPORT - INSTRUCTION DIALOG
    public static CustomDialog instructionDialogInstance(String fragmentTag) {
        CustomDialog fragment = new CustomDialog();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_TAG, fragmentTag);
        fragment.setArguments(args);
        return fragment;
    }

    //ACTIVITIES
    public static CustomDialog activitiesListInstance(String fragmentTag, long day) {
        CustomDialog fragment = new CustomDialog();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_TAG, fragmentTag);
        args.putLong(ARG_DAY, day);
        fragment.setArguments(args);
        return fragment;
    }

    //POMODORO
    public static CustomDialog pomodoroDialogInstance(String fragmentTag) {
        CustomDialog fragment = new CustomDialog();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_TAG, fragmentTag);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_dialog, container, false);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialogTheme);
        addBlurryBackground(view);

        String fragmentTag = getArguments() != null ? getArguments().getString(ARG_FRAGMENT_TAG) : null;

        if (fragmentTag != null) {

            Fragment fragment = null;

            switch (fragmentTag) {
                case "category": //HomeFragment
                    long category_id = getArguments() != null ? getArguments().getLong(ARG_ID) : 0;
                    String categoryName = getArguments() != null ? getArguments().getString(ARG_NAME) : null;
                    fragment = DeleteDialogFragment.newInstance(categoryName, category_id, "category");
                    break;
                case "cardPresenter": //PackageOverview
                    long packId = getArguments() != null ? getArguments().getLong(ARG_ID) : 0;
                    String packageName = getArguments() != null ? getArguments().getString(ARG_NAME) : null;
                    String category = getArguments() != null ? getArguments().getString(ARG_CATEGORY_NAME) : null;
                    fragment = PackageOverviewDialogFragment.newInstance(packageName, packId, category);
                    break;
                case "package": //CardPackageFragment
                    long packageId = getArguments() != null ? getArguments().getLong(ARG_ID) : 0;
                    String name = getArguments() != null ? getArguments().getString(ARG_NAME) : null;
                    fragment = DeleteDialogFragment.newInstance(name, packageId, "package");
                    break;
                case "pauseDialog": //CardPresenter
                    fragment = PauseDialogFragment.newInstance();
                    break;
                case "filterDialog": //HomeFragment, CardPackageFragment
                    String filterTag = getArguments() != null ? getArguments().getString(ARG_FILTER_TAG) : null;
                    String sortTag = getArguments() != null ? getArguments().getString(ARG_SORT_TAG) : null;
                    fragment = FilterDialog.newInstance(getArguments() != null ? getArguments().getString(ARG_TYPE) : null, filterTag, sortTag);
                    break;
                case "loadingSpinner": //ImportFragment
                    fragment = LoadingSpinnerDialogFragment.newInstance();
                    break;
                case "instructionDialog": //ImportFragment
                    fragment = ImportInstructionsFragment.newInstance();
                    break;
                case "activitiesList": //Activities
                    long day = getArguments() != null ? getArguments().getLong(ARG_DAY) : 0;
                    fragment = ActivitiesListFragment.newInstance(day);
                    break;
                case "resetApp":
                    fragment = DeleteDialogFragment.newInstance(null, -1, "reset");
                    break;
                case "pomodoro":
                    fragment = PomodoroWarningDialogFragment.newInstance();
                    break;
                case "deleteFlashcards":
                    fragment = DeleteDialogFragment.newInstance(null, -1, "deleteFlashcards");
                    break;

            }

            if (fragment != null) {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setWindowAnimations(0);
            Window window = getDialog().getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0f);

            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
    }

    private void addBlurryBackground(View view) {
        BlurView blurView = view.findViewById(R.id.blurView);
        float blurRadius = 0.5f;

        View decorView = getActivity().getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);

        Drawable windowBackground = decorView.getBackground();
        if (windowBackground == null) {
            windowBackground = new ColorDrawable(Color.WHITE);
        }

        blurView.setupWith(rootView).setFrameClearDrawable(windowBackground).setBlurRadius(blurRadius).setBlurEnabled(true);
    }
}
