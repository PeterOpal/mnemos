package com.example.bc_praca_x;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;


public class StatisticsFragment extends FragmentSetup {

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if (floatingButtonController != null) {
            Intent addCategoryIntent = new Intent(getActivity(), AddTaskActivity.class);
            floatingButtonController.updateFloatingButton(
                    addCategoryIntent,
                    null,
                    null,
                    true,
                    R.drawable.baseline_add_24);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        replaceFragment(new ActivitiesFragment());

        TabLayout tabsHolder = view.findViewById(R.id.tabLayout);
        tabsHolder.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selectedFragment = null;
                switch (position) {
                    case 0:
                        selectedFragment = new ActivitiesFragment();
                        break;
                    case 1:
                        selectedFragment = new TasksFragment();
                        break;
                }

                replaceFragment(selectedFragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }


    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.activityFragmentContainer, fragment);
        transaction.commit();
    }
}