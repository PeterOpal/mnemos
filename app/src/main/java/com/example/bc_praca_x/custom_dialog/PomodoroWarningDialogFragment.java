package com.example.bc_praca_x.custom_dialog;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bc_praca_x.FragmentSetup;
import com.example.bc_praca_x.R;
import com.example.bc_praca_x.UserSettingsManager;
import com.example.bc_praca_x.helpers.PomodoroMapper;
import com.example.bc_praca_x.helpers.PomodoroTimer;

public class PomodoroWarningDialogFragment extends FragmentSetup {

    public PomodoroWarningDialogFragment() {
        // Required empty public constructor
    }

    public static PomodoroWarningDialogFragment newInstance() {
        return new PomodoroWarningDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pomodoro_warning_dialog, container, false);

        int minutes = PomodoroMapper.getPomodoroTimeInMinutes(settings);

        TextView pomodoroDescription = view.findViewById(R.id.pomodoroDescription);
        pomodoroDescription.setText(getString(R.string.pomodoro_message, minutes+" min"));

        Button okButton = view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getActivity() != null) {
                    PomodoroTimer.getInstance().startTimer(getActivity(), (long) minutes * 60 * 1000);
                }

                DialogFragment parentDialog = (DialogFragment) getParentFragment();
                if (parentDialog != null) parentDialog.dismiss();
            }
        });


        return view;
    }
}