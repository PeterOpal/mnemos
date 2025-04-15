package com.example.bc_praca_x;

import android.graphics.Color;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bc_praca_x.custom_dialog.CustomDialog;
import com.example.bc_praca_x.database.viewmodel.ActivityViewModel;
import com.example.bc_praca_x.models.ExtraChartData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivitiesFragment extends FragmentSetup {

    private TextView weekTextView;
    private SimpleDateFormat sdf;
    private Calendar calendar;
    private SimpleDateFormat sdfToConvertToDay;
    private BarChart barChart;
    private ActivityViewModel activityViewModel;
    private TextView frequentCategoryAndPackTextView, totalTimeSpentTextView;
    private UserSettingsManager userSettingsManager;

    public ActivitiesFragment() {
        // Required empty public constructor
    }

    public static ActivitiesFragment newInstance(String param1, String param2) {
        ActivitiesFragment fragment = new ActivitiesFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_activities, container, false);

        weekTextView = view.findViewById(R.id.weekText);
        Button prevButton = view.findViewById(R.id.prevButton);
        Button nextButton = view.findViewById(R.id.nextButton);
        barChart = view.findViewById(R.id.chart);
        activityViewModel =  new ViewModelProvider(this).get(ActivityViewModel.class);
        frequentCategoryAndPackTextView = view.findViewById(R.id.frequentCatAndPack);
        totalTimeSpentTextView = view.findViewById(R.id.totalTimeByWeek);
        userSettingsManager = new UserSettingsManager(getContext());

        String localeCode = userSettingsManager.getSetting("app_language");
        if(localeCode.equals("ENGLISH")) localeCode = "en";
        else if(localeCode.equals("SLOVAK")) localeCode = "sk";

        sdf = new SimpleDateFormat("d MMM", new Locale(localeCode, localeCode.toUpperCase()));
        sdfToConvertToDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        calendar = Calendar.getInstance();

        nextButton.setOnClickListener(v -> {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            updateWeekText();
        });

        prevButton.setOnClickListener(v -> {
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            updateWeekText();
        });

        updateWeekText();

        return view;
    }

    private void updateWeekText() {
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long weekStartForQuery = calendar.getTimeInMillis();
        String weekStart = sdf.format(calendar.getTime());


        calendar.add(Calendar.DAY_OF_WEEK, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        long weekEndForQuery = calendar.getTimeInMillis();
        String weekEnd = sdf.format(calendar.getTime());

        weekTextView.setText(String.format("%s - %s", weekStart, weekEnd));

        calendar.add(Calendar.DAY_OF_WEEK, -6);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        displayBarChart(weekStartForQuery, weekEndForQuery);
        setInfoBar(weekStartForQuery, weekEndForQuery);
    }


    private void displayBarChart(long from, long to) {

        final int DAYS_IN_WEEK = 7;
        int[] totalTime = new int[DAYS_IN_WEEK];
        int[] count = new int[DAYS_IN_WEEK];
        long[] dates = new long[DAYS_IN_WEEK];
        final String[] weekdays = new String[]{getString(R.string.chart_monday), getString(R.string.chart_tuesday), getString(R.string.chart_wednesday), getString(R.string.chart_thursday), getString(R.string.chart_friday), getString(R.string.chart_saturday), getString(R.string.chart_sunday)};
        activityViewModel.getDailyActivitySummary(from, to).observe(getViewLifecycleOwner(), dailyActivitySummaries -> {
            if (dailyActivitySummaries != null) {
                for (int i = 0; i < dailyActivitySummaries.size(); i++) {
                    try {
                        Date date = sdfToConvertToDay.parse(dailyActivitySummaries.get(i).day);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setFirstDayOfWeek(Calendar.MONDAY);
                        calendar.setTime(date);
                        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                        long timeForQuery = calendar.getTimeInMillis();

                        int index = (dayOfWeek + 5) % 7;

                        totalTime[index] += convertoMinutes(dailyActivitySummaries.get(i).totalTime);
                        count[index] += dailyActivitySummaries.get(i).count;
                        dates[index] = timeForQuery;

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                List<BarEntry> entries = new ArrayList<>();
                for (int i = 0; i < DAYS_IN_WEEK; i++) {
                    ExtraChartData extraChartData = new ExtraChartData(count[i], dates[i]);
                    entries.add(new BarEntry((float)i, (float)totalTime[i], extraChartData));
                }

                BarDataSet dataSet = new BarDataSet(entries, "");
                barChart.getDescription().setEnabled(false);
                dataSet.setDrawValues(true);

                //TOP COLUMN VALUE LABEL
                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getBarLabel(BarEntry entry) {
                        Object extraData = entry.getData();
                        if (extraData instanceof ExtraChartData) {
                            return (((ExtraChartData) extraData).count + " x");
                        }
                        return "";
                    }
                });

                dataSet.setValueTextSize(12f);

                Legend legend = barChart.getLegend();
                LegendEntry customEntry = new LegendEntry();
                customEntry.form = Legend.LegendForm.SQUARE;
                customEntry.formSize = 10f;
                customEntry.formColor = Color.BLACK;
                customEntry.label = getString(R.string.activities_chart_label);
                legend.setCustom(Collections.singletonList(customEntry));

                BarData barData = new BarData(dataSet);
                barChart.setData(barData);

                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(weekdays));
                xAxis.setGranularity(1f);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                YAxis leftAxis = barChart.getAxisLeft();
                leftAxis.setAxisMinimum(0f);

                YAxis rightAxis = barChart.getAxisRight();
                rightAxis.setEnabled(false);

                leftAxis.setGranularityEnabled(true);

                leftAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return (int)value + " min";
                    }
                });

                setupChartListener();

                barChart.invalidate();
            }
        });
    }

    private void setupChartListener(){
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int dayIndex = (int) e.getX();

                ExtraChartData extraData = (ExtraChartData) e.getData();
                if(extraData.count == 0) return;

                CustomDialog dialog = CustomDialog.activitiesListInstance("activitiesList", extraData.date);
                dialog.show(getChildFragmentManager(), "custom_dialog");
            }

            @Override
            public void onNothingSelected() {}
        });
    }

    private int convertoMinutes(int seconds) {
        return seconds / 60;
    }


    private void setInfoBar(long from, long to) {
        activityViewModel.getTotalTimeSpentByWeek(from, to).observe(getViewLifecycleOwner(), totalTimeByWeek -> {
            if (totalTimeByWeek != null) {
                totalTimeSpentTextView.setText(totalTimeByWeek+"");
            }
        });

        activityViewModel.getFrequentPackageWithCategory(from, to).observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                frequentCategoryAndPackTextView.setText(data.categoryName + " -> " + data.cardPackName);
            } else{
                frequentCategoryAndPackTextView.setText("---");
            }
        });
    }
}