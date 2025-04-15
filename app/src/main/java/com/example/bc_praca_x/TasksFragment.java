package com.example.bc_praca_x;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bc_praca_x.adapters.ScheduledTaskAdapter;
import com.example.bc_praca_x.database.POJO.TaskWithPack;
import com.example.bc_praca_x.database.viewmodel.TaskViewModel;
import com.example.bc_praca_x.models.ScheduledTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TasksFragment extends FragmentSetup {

    private TextView weekTextView;
    private Calendar calendar;
    private SimpleDateFormat sdf;
    private SimpleDateFormat sdfForQuery;
    private TaskViewModel taskViewModel;
    private RecyclerView tasksRecycler;
    private List<ScheduledTask> scheduledTasks;
    private ScheduledTaskAdapter adapter;
    private UserSettingsManager userSettingsManager;
    private String localeCode;

    public TasksFragment() {
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
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        scheduledTasks = new ArrayList<>();

        tasksRecycler = view.findViewById(R.id.tasksRecyclerView);
        weekTextView = view.findViewById(R.id.weekText);
        Button prevButton = view.findViewById(R.id.prevButton);
        Button nextButton = view.findViewById(R.id.nextButton);
        userSettingsManager = new UserSettingsManager(getContext());

        localeCode = userSettingsManager.getSetting("app_language");
        if(localeCode.equals("ENGLISH")) localeCode = "en";
        else if(localeCode.equals("SLOVAK")) localeCode = "sk";

        sdf = new SimpleDateFormat("d MMM",  new Locale(localeCode, localeCode.toUpperCase()));
        sdfForQuery = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        calendar = Calendar.getInstance();
        updateWeekText();

        tasksRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new ScheduledTaskAdapter(scheduledTasks);
        tasksRecycler.setAdapter(adapter);

        nextButton.setOnClickListener(v -> {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            updateWeekText();
        });

        prevButton.setOnClickListener(v -> {
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            updateWeekText();
        });

        return view;
    }

    private void updateWeekText() {
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String weekStart = sdf.format(calendar.getTime());
        String weekStartForQuery = sdfForQuery.format(calendar.getTime());

        calendar.add(Calendar.DAY_OF_WEEK, 6);
        String weekEnd = sdf.format(calendar.getTime());
        String weekEndForQuery = sdfForQuery.format(calendar.getTime());

        weekTextView.setText(String.format("%s - %s", weekStart, weekEnd));
        calendar.add(Calendar.DAY_OF_WEEK, -6);

        getTasks(weekStartForQuery, weekEndForQuery);
    }

    private void getTasks(String weekStart, String weekEnd) {
        taskViewModel.getTasksFromTo(weekStart, weekEnd).observe(getViewLifecycleOwner(), tasks -> {
            scheduledTasks.clear();

            Map<String, Map<String, List<ScheduledTask>>> groupedTasks = new LinkedHashMap<>();

            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdfDisplay = new SimpleDateFormat("EEEE, d MMM", new Locale(localeCode, localeCode.toUpperCase()));

            Calendar todayDate = Calendar.getInstance();
            String today = sdfDate.format(todayDate.getTime());
            Calendar tomorrowDate = (Calendar) todayDate.clone();
            tomorrowDate.add(Calendar.DAY_OF_YEAR, 1);
            String tomorrow = sdfDate.format(tomorrowDate.getTime());

            for (TaskWithPack task : tasks) {
                String taskDateKey = sdfDate.format(task.task.reviewDate);
                String displayDate = sdfDisplay.format(task.task.reviewDate);

                if (taskDateKey.equals(today)) displayDate = getString(R.string.Today);
                else if (taskDateKey.equals(tomorrow)) displayDate = getString(R.string.Tomorrow);

                String categoryName = "---";
                if (task.category != null) categoryName = task.category.getName();

                String taskDescription = "";
                boolean userTask = false;
                if(task.task.cardsReviewCount == -1) { taskDescription = task.task.getTaskName(); userTask = true;}
                else taskDescription = task.task.cardsReviewCount + getString(R.string.cards_to_review);

                groupedTasks.putIfAbsent(displayDate, new LinkedHashMap<>());
                groupedTasks.get(displayDate).putIfAbsent(categoryName, new ArrayList<>());

                groupedTasks.get(displayDate).get(categoryName).add(new ScheduledTask(task.cardPack.name, task.task.getId() ,task.cardPack.id, taskDescription, task.task.completed, false, "", null, userTask));
            }

            //SORTING DATES
            List<String> sortedDates = new ArrayList<>(groupedTasks.keySet());
            Collections.sort(sortedDates, (date1, date2) -> {
                try {
                    return sdfDisplay.parse(date1).compareTo(sdfDisplay.parse(date2));
                } catch (Exception e) {
                    return 0;
                }
            });

            //FOR UI
            for (String sortedDate : sortedDates) {
                scheduledTasks.add(new ScheduledTask("", -1, -1,  "", false, true, sortedDate, null, false));

                for (Map.Entry<String, List<ScheduledTask>> categoryEntry : groupedTasks.get(sortedDate).entrySet()) {
                    scheduledTasks.add(new ScheduledTask(categoryEntry.getKey(), -1, -1, "", false, false, "",
                            categoryEntry.getValue() != null ? categoryEntry.getValue() : new ArrayList<>(), false));
                }
            }

            adapter.notifyDataSetChanged();
        });
    }
}