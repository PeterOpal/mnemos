package com.example.bc_praca_x.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.bc_praca_x.database.DBHelper;
import com.example.bc_praca_x.database.POJO.ActivityWithCategoryAndPackage;
import com.example.bc_praca_x.database.POJO.ActivityWithDetails;
import com.example.bc_praca_x.database.POJO.DailyActivitySummary;
import com.example.bc_praca_x.database.dao.ActivityDao;
import com.example.bc_praca_x.database.entity.Activity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityRepository {

    private final ActivityDao activityDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ActivityRepository(Application application) {
        DBHelper database = DBHelper.getInstance(application);
        activityDao = database.activityDao();
    }

    public void insertActivity(Activity activity) {
        executorService.execute(() -> activityDao.insert(activity));
    }

    public LiveData<Integer> getTotalTimeSpent() {
        return activityDao.getTotalTimeSpent();
    }

    public LiveData<Integer> getTotalTimeSpentForCategories(List<Long> categoryIds) {
        return activityDao.getTotalTimeSpentForCategories(categoryIds);
    }

    public LiveData<List<DailyActivitySummary>> getDailyActivitySummary(long from, long to) {
        return activityDao.getDailyActivitySummary(from, to);
    }

    public LiveData<Integer> getTotalTimeSpentByWeek(long from, long to){
        return activityDao.getTotalTimeSpentByWeek(from, to);
    }

    public LiveData<ActivityWithCategoryAndPackage> getFrequentPackageWithCategory(long from, long to){
        return activityDao.getFrequentPackageWithCategory(from, to);
    }

    public LiveData<Integer> getPackageTotalTimeSpent(long packageId){
        return activityDao.getPackageTotalTimeSpent(packageId);
    }

    public LiveData<String> getLastTimeLeartDate(long packageId){
        return activityDao.getLastTimeLeartDate(packageId);
    }

    public LiveData<List<ActivityWithDetails>> getActivitiesByDay(long day){
        return activityDao.getActivitiesByDay(day);
    }

}
