package com.example.bc_praca_x;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bc_praca_x.custom_dialog.CustomDialog;
import com.example.bc_praca_x.database.viewmodel.ActivityViewModel;
import com.example.bc_praca_x.database.viewmodel.CardViewModel;
import com.example.bc_praca_x.database.viewmodel.TaskViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;


public class PackageOverview extends FragmentSetup {
    private long packId;
    private String packName, categoryName;
    private CardViewModel cardViewModel;
    private TaskViewModel taskViewModel;
    private ActivityViewModel activityViewModel;
    private LinearLayout editCardsButton;
    private PieChart pieChart;

    public PackageOverview() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packId = getArguments() != null ? getArguments().getLong("package_id", 1) : -1;
        packName = getArguments() != null ? getArguments().getString("package_name") : "";
        categoryName = getArguments() != null ? getArguments().getString("category_name") : "";
    }

    @Override
    public void onResume() {
        super.onResume();

        if (floatingButtonController != null) {
            floatingButtonController.updateFloatingButton(
                    null,
                    null,
                    () -> {
                        CustomDialog dialog = CustomDialog.newInstance("cardPresenter", packId, packName, categoryName);
                        dialog.show(getChildFragmentManager(), "custom_dialog");
                    },
                    true,
                    R.drawable.baseline_start_24);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_package_overview, container, false);

        editCardsButton = view.findViewById(R.id.editCardButton);
        editCardsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CardBuilderActivity.class);
            intent.putExtra("update", true);
            intent.putExtra("cardPackId", packId);
            startActivity(intent);
        });

        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);
        setInfoBar(view);

        TextView packageName = view.findViewById(R.id.breadcrumbs);
        packageName.setText(categoryName+" -> "+packName);
        pieChart = view.findViewById(R.id.pieChart);

        addPieChart(view);

        return view;
    }

    private void addPieChart(View view) {
        cardViewModel.getCardsByCardPackId(packId).observe(getViewLifecycleOwner(), cards -> {
            int notLearnt = 0, slightlyLearnt = 0, learnt = 0;
            for (int i = 0; i < cards.size(); i++) {
                double EF = cards.get(i).getEaseFactor();

                if(EF < 2.6) {
                    notLearnt++;
                } else if (EF >= 2.6 && EF < 2.8) {
                    slightlyLearnt++;
                } else if (EF >= 2.8) {
                    learnt++;
                }
            }

            List<PieEntry> entries = new ArrayList<>();
            List<Integer> colors = new ArrayList<>();

            if (notLearnt > 0) {
                entries.add(new PieEntry(notLearnt, getString(R.string.not_learnt)));
                colors.add(Color.parseColor("#FF746C"));
            }
            if (slightlyLearnt > 0) {
                entries.add(new PieEntry(slightlyLearnt, getString(R.string.slightly_learnt)));
                colors.add(Color.parseColor("#FFFFC5"));
            }
            if (learnt > 0) {
                entries.add(new PieEntry(learnt, getString(R.string.learnt)));
                colors.add(Color.parseColor("#6FC276"));
            }

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(colors);
            dataSet.setValueTextSize(18f);
            dataSet.setValueTextColor(Color.BLACK);
            pieChart.setEntryLabelColor(Color.BLACK);

            dataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value);
                }
            });

            PieData pieData = new PieData(dataSet);
            pieChart.setData(pieData);
            pieChart.setCenterText(getString(R.string.cards_status));
            pieChart.invalidate();
            pieChart.getDescription().setEnabled(false);
        });
    }

    private void setInfoBar(View view){
        cardViewModel.getCardCount(packId).observe(getViewLifecycleOwner(), count -> {
            TextView cardCount = view.findViewById(R.id.cards_count);
            cardCount.setText(String.valueOf(count));

            if(count == 0) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        activityViewModel.getPackageTotalTimeSpent(packId).observe(getViewLifecycleOwner(), totalTime -> {
            TextView timeSpent = view.findViewById(R.id.packageLearningTime);
            timeSpent.setText(totalTime != null ? totalTime : "0m");
        });

        activityViewModel.getLastTimeLeartDate(packId).observe(getViewLifecycleOwner(), date -> {
            TextView lastLearned = view.findViewById(R.id.lastReviewDate);
            lastLearned.setText(date != null ? date : "---");
        });

        taskViewModel.getOptimalDateToReviewPackage(packId).observe(getViewLifecycleOwner(), date -> {
            TextView optimalDate = view.findViewById(R.id.calculatedReviewDate);
            optimalDate.setText(date != null ? date : "---");
        });



    }
}