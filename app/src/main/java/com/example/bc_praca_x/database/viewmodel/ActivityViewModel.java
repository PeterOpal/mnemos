package com.example.bc_praca_x.database.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.bc_praca_x.database.POJO.ActivityWithCategoryAndPackage;
import com.example.bc_praca_x.database.POJO.ActivityWithDetails;
import com.example.bc_praca_x.database.POJO.DailyActivitySummary;
import com.example.bc_praca_x.database.entity.Activity;
import com.example.bc_praca_x.database.repository.ActivityRepository;

import java.util.List;

public class ActivityViewModel extends AndroidViewModel {
    private final ActivityRepository repository;

    public ActivityViewModel(Application application) {
        super(application);
        repository = new ActivityRepository(application);
    }

    public void insertActivity(Activity activity) {
        repository.insertActivity(activity);
    }

    public LiveData<String> getTotalTimeSpentFormatted() {
        return Transformations.map(repository.getTotalTimeSpent(), this::formatTime);
    }

    public LiveData<String> getCategoryTotalTimeSpentFormatted(List<Long> ids){
        return Transformations.map(repository.getTotalTimeSpentForCategories(ids), this::formatTime);
    }

    public LiveData<String> getPackageTotalTimeSpent(long packageId){
        return Transformations.map(repository.getPackageTotalTimeSpent(packageId), this::formatTime);
    }

    private String formatTime(Integer totalSeconds){
        int seconds = (totalSeconds != null) ? totalSeconds : 0;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;

        if(hours > 0)
            return String.format("%02dh %02dm", hours, minutes);
        else
            return minutes + " m";
    }

    public LiveData<List<DailyActivitySummary>> getDailyActivitySummary(long from, long to){
        return repository.getDailyActivitySummary(from, to);
    }

    public LiveData<String> getTotalTimeSpentByWeek(long from, long to){
        return Transformations.map(repository.getTotalTimeSpentByWeek(from,to), this::formatTime);
    }

    public LiveData<ActivityWithCategoryAndPackage> getFrequentPackageWithCategory(long from, long to){
        return repository.getFrequentPackageWithCategory(from, to);
    }

    public LiveData<String> getLastTimeLeartDate(long packageId){
        return repository.getLastTimeLeartDate(packageId);
    }

    public LiveData<List<ActivityWithDetails>> getActivitiesByDay(long day){
        return repository.getActivitiesByDay(day);
    }
}
