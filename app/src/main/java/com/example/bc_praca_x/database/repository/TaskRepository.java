package com.example.bc_praca_x.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.bc_praca_x.database.DBHelper;
import com.example.bc_praca_x.database.POJO.TaskWithPack;
import com.example.bc_praca_x.database.dao.TaskDao;
import com.example.bc_praca_x.database.entity.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {

    public final TaskDao taskDao;
    public final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public TaskRepository(Application application) {
        DBHelper database = DBHelper.getInstance(application);
        taskDao = database.taskDao();
    }

    public void insertTask(Task task) {
        executorService.execute(() -> taskDao.insert(task));
    }

    public LiveData<List<TaskWithPack>> getTasksFromTo(String from, String to) {
        return taskDao.getTasksFromTo(from, to);
    }

    public Task existingTaskByDayAndPackId(Date reviewDate, long packId) {
        return taskDao.existingTaskByDayAndPackId(sdf.format(reviewDate), packId);
    }

    public void updateTask(Task task) {
        executorService.execute(() -> taskDao.update(task));
    }

    public Task getTaskByDayAndPackId(Date today, long packId) {
        return taskDao.getTaskByDayAndPackId(sdf.format(today), packId);
    }

    public void deleteTask(Task task) {
        executorService.execute(() -> taskDao.delete(task));
    }

    public LiveData<String> getOptimalDateToReviewPackage(long packId) {
        return taskDao.getOptimalDateToReviewPackage(packId);
    }

    public void markAsDone(long taskId) {
        executorService.execute(() -> taskDao.markAsDone(taskId));
    }

}
