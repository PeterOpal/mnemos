package com.example.bc_praca_x.custom_dialog;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.example.bc_praca_x.R;
import com.example.bc_praca_x.helpers.Timer;

public class LoadingSpinnerDialogFragment extends Fragment {
    private Timer timer;
    private Handler handler;
    private final int timeout = 40;

    public LoadingSpinnerDialogFragment() {
        // Required empty public constructor
    }

    public static LoadingSpinnerDialogFragment newInstance() {
        return new LoadingSpinnerDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading_spinner_dialog, container, false);

        timer = new Timer();
        timer.start();
        handler = new Handler(Looper.getMainLooper());

        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(view.findViewById(R.id.loadingIndicator), "progress", 0, 100);
        progressAnimator.setDuration(2000); // 2 sec
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.start();

        checkTimeout();

        return view;
    }

    private void checkTimeout() {
        handler.postDelayed(() -> {
            if (timer.getElapsedTime() >= timeout && getContext() != null) {
                Toast.makeText(getContext(), getString(R.string.error_import), Toast.LENGTH_SHORT).show();
                DialogFragment parentDialog = (DialogFragment) getParentFragment();
                if (parentDialog != null) parentDialog.dismiss();
            }
        }, timeout * 1000L);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}