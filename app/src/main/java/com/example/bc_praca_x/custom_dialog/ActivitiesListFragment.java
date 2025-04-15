package com.example.bc_praca_x.custom_dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bc_praca_x.FragmentSetup;
import com.example.bc_praca_x.R;
import com.example.bc_praca_x.adapters.ActivityAdapter;
import com.example.bc_praca_x.database.POJO.ActivityWithDetails;
import com.example.bc_praca_x.database.viewmodel.ActivityViewModel;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class ActivitiesListFragment extends FragmentSetup {

    private long day;
    private ActivityViewModel activityViewModel;
    private List<ActivityWithDetails> activities;
    private ActivityAdapter activityAdapter;

    public ActivitiesListFragment() {
        // Required empty public constructor
    }


    public static ActivitiesListFragment newInstance(long day) {
        ActivitiesListFragment fragment = new ActivitiesListFragment();
        Bundle args = new Bundle();
        args.putLong("day", day);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            day = getArguments().getLong("day");
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities_list, container, false);

        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);
        activities = new ArrayList<>();

        TextView dateText = view.findViewById(R.id.activitiesText);
        dateText.setText(dateText.getText() + " - " +formatDateByTimeZone());

        Button closeBtn = view.findViewById(R.id.closeButton);
        closeBtn.setOnClickListener(v -> {
            DialogFragment parentDialog = (DialogFragment) getParentFragment();
            if (parentDialog != null) parentDialog.dismiss();
        });

        RecyclerView activitiesList = view.findViewById(R.id.activitiesList);
        activitiesList.setLayoutManager(new LinearLayoutManager(getContext()));

        activityAdapter = new ActivityAdapter(getContext(), activities);
        activitiesList.setAdapter(activityAdapter);

        getActivities();

        return view;
    }

    private void getActivities(){
        activityViewModel.getActivitiesByDay(day).observe(getViewLifecycleOwner(), activitiesFromDb -> {
            activities.clear();
            for (int i = 0; i < activitiesFromDb.size(); i++) {
                activities.add(activitiesFromDb.get(i));
            }

            activityAdapter.notifyDataSetChanged();
        });
    }
    
    private String formatDateByTimeZone(){
        Instant instant = Instant.ofEpochMilli(day);
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, zone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd");
        return dateTime.format(formatter);
    }

}