package com.example.bc_praca_x.helpers;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.example.bc_praca_x.database.entity.Card;
import com.example.bc_praca_x.database.entity.Task;
import com.example.bc_praca_x.database.viewmodel.CardViewModel;
import com.example.bc_praca_x.database.viewmodel.TaskViewModel;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskHelper {
    private ExecutorService executorService;
    private final TaskViewModel taskViewModel;

    public TaskHelper(TaskViewModel taskViewModel) {
        executorService = Executors.newSingleThreadExecutor();
        this.taskViewModel = taskViewModel;
    }

    //AFTER REVIEWING CARD AND CALCULATING NEXT REVIEW DATE BY ALGORITHM WE NEED TO SAVE OR UPDATE TASK
    public void saveOrUpdateTask(Card card) {
        executorService.execute(() -> {

            removeCardFromTaskIfReviwed(card);

            Task task = taskViewModel.existingTaskByDayAndPackId(card.nextReviewDate, card.cardPackId);
            if (task != null) { // IF TASK EXISTS WE NEED TO UPDATE THE CARD REVIEW COUNT
                task.cardsReviewCount++;
                taskViewModel.updateTask(task);
            } else { // TASK DOESN'T EXIST
                taskViewModel.insertTask(new Task(null, card.nextReviewDate, 1, false, card.cardPackId));
            }
        });
    }

    private void removeCardFromTaskIfReviwed(Card card) {
        Task task = taskViewModel.getTaskByDayAndPackId(new Date(), card.cardPackId);
        if (task != null) {
            task.cardsReviewCount--;
            if (task.cardsReviewCount == 0) {
                taskViewModel.deleteTask(task);
            } else {
                taskViewModel.updateTask(task);
            }
        }
    }

    public void syncCardWithTask(long cardId, CardViewModel cardViewModel, LifecycleOwner lifecycleOwner) {
        cardViewModel.getCardById(cardId).observe(lifecycleOwner, card -> {
            if (card != null) {

                executorService.execute(() -> {
                    Task task = taskViewModel.getTaskByDayAndPackId(card.nextReviewDate, card.cardPackId);
                    if (task != null) {
                        task.cardsReviewCount--;

                        if (task.cardsReviewCount == 0) {
                            taskViewModel.deleteTask(task);
                        } else {
                            taskViewModel.updateTask(task);
                        }
                    }
                });
            }
        });
    }
}
