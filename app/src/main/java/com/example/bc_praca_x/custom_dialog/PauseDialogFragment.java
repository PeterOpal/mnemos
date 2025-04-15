package com.example.bc_praca_x.custom_dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.bc_praca_x.MainActivity;
import com.example.bc_praca_x.R;
import com.example.bc_praca_x.helpers.Timer;

public class PauseDialogFragment extends Fragment {

    private Timer timer;

    public PauseDialogFragment() {
        // Required empty public constructor
    }

    public static PauseDialogFragment newInstance() {
        PauseDialogFragment fragment = new PauseDialogFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            this.timer = activity.getTimer();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pause_dialog, container, false);

        Button resume = view.findViewById(R.id.resumeButton);
        resume.setOnClickListener(v -> {
            timer.resume();
            DialogFragment parentDialog = (DialogFragment) getParentFragment();
            if (parentDialog != null) parentDialog.dismiss();
        });

        Button finishLearningSessionButton = view.findViewById(R.id.finishLearningSessionButton);
        finishLearningSessionButton.setOnClickListener(v -> {

            Bundle result = new Bundle();
            result.putBoolean("sessionFinished", true);
            requireActivity().getSupportFragmentManager().setFragmentResult("pause_dialog_session_finished", result);
            DialogFragment parentDialog = (DialogFragment) getParentFragment();
            if (parentDialog != null) parentDialog.dismiss();

        });

        return view;
    }
}