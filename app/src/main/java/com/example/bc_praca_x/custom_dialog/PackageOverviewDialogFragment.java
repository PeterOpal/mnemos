package com.example.bc_praca_x.custom_dialog;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bc_praca_x.CardPresenterFragment;
import com.example.bc_praca_x.FragmentSetup;
import com.example.bc_praca_x.R;
import com.example.bc_praca_x.database.viewmodel.CardViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PackageOverviewDialogFragment extends FragmentSetup {

    private String packageName, categoryName;
    private long packId;
    private Button cancelButton, startButton;
    private Spinner modeSpinner, orderSpinner;
    private List<String> orderValues, modeValues;
    private CardViewModel cardViewModel;

    public PackageOverviewDialogFragment() {
        // Required empty public constructor
    }

    public static PackageOverviewDialogFragment newInstance(String packageName, long packId, String categoryName) {
        PackageOverviewDialogFragment fragment = new PackageOverviewDialogFragment();
        Bundle args = new Bundle();
        args.putString("packageName", packageName);
        args.putLong("packId", packId);
        args.putString("categoryName", categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            packageName = getArguments().getString("packageName").toUpperCase();
            packId = getArguments().getLong("packId");
            categoryName = getArguments().getString("categoryName");
            Log.d("PackageOverviewDialog", "onCreate: " + packageName + " " + packId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_package_overview_dialog, container, false);

        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);

        TextView textView = view.findViewById(R.id.dialogPackName);
        textView.setText(packageName);

        orderSpinner = view.findViewById(R.id.orderSpinner);
        modeSpinner = view.findViewById(R.id.modeSpinner);
        addValuesToSpinners();

        cancelButton = view.findViewById(R.id.cancelButton);
        startButton = view.findViewById(R.id.startLearningButton);
        attachButtonListeners();

        return view;
    }

    private void attachButtonListeners(){
        cancelButton.setOnClickListener(v -> {
            DialogFragment parentDialog = (DialogFragment) getParentFragment();
            if (parentDialog != null) parentDialog.dismiss();
        });

        startButton.setOnClickListener(v -> {
            Fragment cardPresenter = new CardPresenterFragment();
            Bundle args = new Bundle();
            args.putString("packageName", packageName);
            args.putString("categoryName", categoryName);
            args.putLong("pack_id", packId);
            args.putInt("mode",  (Integer) modeSpinner.getSelectedItemPosition());
            args.putInt("order", (Integer) orderSpinner.getSelectedItemPosition());
            cardPresenter.setArguments(args);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, cardPresenter)
                    .addToBackStack(null)
                    .commit();

            DialogFragment parentDialog = (DialogFragment) getParentFragment();
            if (parentDialog != null) parentDialog.dismiss();
        });
    }

    private void addValuesToSpinners(){
        orderValues = new ArrayList<>(Arrays.asList(getString(R.string.created_at), getString(R.string.random)));
        modeValues = new ArrayList<>(Arrays.asList(getString(R.string.FREE_MODE_WITH_LEARNING), getString(R.string.FREE_MODE)));

        //check if one of the cards should be learned with the algorithm
        cardViewModel.getReviewableCardsCountByPackId(packId).observe(getViewLifecycleOwner(), count -> {
            if (count > 0) {
                modeValues.add(getString(R.string.ALGORITHM) + " - " + count + " " + getString(R.string.cards));
            }
        });

        ArrayAdapter<String> orderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, orderValues);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(orderAdapter);

        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, modeValues);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(modeAdapter);
    }
}