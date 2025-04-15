package com.example.bc_praca_x.database.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.bc_praca_x.database.POJO.TaskWithPack;
import com.example.bc_praca_x.database.entity.Task;
import com.example.bc_praca_x.database.repository.TaskRepository;

import java.util.Date;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository repository;

    public TaskViewModel (Application application) {
        super(application);
        repository = new TaskRepository(application);
    }

    public void insertTask(Task task) {
        repository.insertTask(task);
    }

    public LiveData<List<TaskWithPack>> getTasksFromTo(String from, String to) {
        return repository.getTasksFromTo(from, to);
    }

    public Task existingTaskByDayAndPackId(Date reviewDate, long packId) {
        return repository.existingTaskByDayAndPackId(reviewDate, packId);
    }

    public LiveData<String> getOptimalDateToReviewPackage(long packId) {
        return repository.getOptimalDateToReviewPackage(packId);
        //return Transformations.map(repository.getOptimalDateToReviewPackage(packId), this::formatTime);
    }

    public void updateTask(Task task) {
        repository.updateTask(task);
    }

    public Task getTaskByDayAndPackId(Date today, long packId) {
        Log.d("TTaskHelper", "Task found: " + "as");
        return repository.getTaskByDayAndPackId(today, packId);
    }

    public void deleteTask(Task task) {
        repository.deleteTask(task);
    }

    public void markAsDone(long taskId) {
        repository.markAsDone(taskId);
    }

}
