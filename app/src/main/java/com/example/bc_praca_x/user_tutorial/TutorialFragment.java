package com.example.bc_praca_x.user_tutorial;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bc_praca_x.R;
import com.example.bc_praca_x.UserSettingsManager;

public class TutorialFragment extends Fragment {

    private int pageNumber;
    private String username;

    public TutorialFragment() {
        // Required empty public constructor
    }

    public static TutorialFragment newInstance(int pageNumber) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt("pageNumber", pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageNumber = getArguments().getInt("pageNumber");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        UserSettingsManager userSettingsManager = new UserSettingsManager(getContext());
        username = userSettingsManager.getSetting(UserSettingsManager.USERNAME_KEY);

        int layoutRes = R.layout.fragment_tutorial;

        switch (pageNumber) {
            case 1:
                layoutRes = R.layout.fragment_tutorial_2;
                break;
            case 2:
                layoutRes = R.layout.fragment_tutorial_3;
                break;
        }

        View view = inflater.inflate(layoutRes, container, false);

        if (pageNumber == 0) {

            TextView welcomeText = view.findViewById(R.id.welcomeTextView);
            welcomeText.setText(
                    getString(R.string.Welcome, username)
            );

            ImageView imageView = view.findViewById(R.id.welcome_icon);
            animateImageView(imageView);
        }

        if (pageNumber == 2) {
            CheckBox checkBox = view.findViewById(R.id.checkBoxSampleContent);
            TutorialViewModel tutorialViewModel = new ViewModelProvider(requireActivity()).get(TutorialViewModel.class);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                tutorialViewModel.setAddSampleContent(isChecked);
            });
        }

        return view;
    }

    private void animateImageView(ImageView imageView) {
        if (imageView != null) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", -3f, 3f);
            animator.setDuration(1800);
            animator.setRepeatCount(ObjectAnimator.INFINITE);
            animator.setRepeatMode(ObjectAnimator.REVERSE);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
        }
    }

}