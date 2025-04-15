package com.example.bc_praca_x.custom_dialog;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bc_praca_x.R;

public class ImportInstructionsFragment extends Fragment {

    public ImportInstructionsFragment() {
        // Required empty public constructor
    }

    public static ImportInstructionsFragment newInstance() {
        return new ImportInstructionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_import_instructions, container, false);

        view.findViewById(R.id.okButton).setOnClickListener(v -> {
            DialogFragment parentDialog = (DialogFragment) getParentFragment();
            if (parentDialog != null) parentDialog.dismiss();
        });

        return view;
    }
}