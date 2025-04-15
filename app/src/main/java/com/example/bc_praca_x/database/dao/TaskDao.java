package com.example.bc_praca_x.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.bc_praca_x.database.POJO.TaskWithPack;
import com.example.bc_praca_x.database.entity.Task;
import java.util.List;

@Dao
public interface TaskDao {

    //@Query("SELECT * FROM tasks WHERE card_packs_id = :packId AND reviewDate = :reviewDate LIMIT 1")
    @Query("SELECT * FROM tasks WHERE card_packs_id = :packId AND strftime('%Y-%m-%d', reviewDate / 1000, 'unixepoch') = :reviewDate LIMIT 1")
    Task existingTaskByDayAndPackId(String reviewDate, long packId);

    //@Query("SELECT * FROM tasks WHERE completed = 0 AND reviewDate BETWEEN :from AND :to")
    //@Query("SELECT * FROM tasks WHERE completed = 0 AND strftime('%Y-%m-%d', reviewDate / 1000, 'unixepoch') BETWEEN :from AND :to")
    //LiveData<List<TaskWithPack>> getTasksFromTo(String from, String to);
    @Transaction
    @Query("SELECT tasks.*, card_packs.*, categories.* FROM tasks " +
            "JOIN card_packs ON tasks.card_packs_id = card_packs.id " +
            "JOIN categories ON card_packs.card_category_id = categories.id " +
            "WHERE strftime('%Y-%m-%d', reviewDate / 1000, 'unixepoch') BETWEEN :from AND :to")
    LiveData<List<TaskWithPack>> getTasksFromTo(String from, String to);

    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Query("SELECT * FROM tasks WHERE card_packs_id = :packId AND strftime('%Y-%m-%d', reviewDate / 1000, 'unixepoch') <= :day AND taskName IS NULL LIMIT 1")
    Task getTaskByDayAndPackId(String day, long packId);

    @Delete
    void delete(Task task);

    @Query("SELECT strftime('%d.%m.%Y', reviewDate / 1000, 'unixepoch') FROM tasks WHERE card_packs_id = :packId AND taskName IS NULL ORDER BY reviewDate ASC LIMIT 1")
    LiveData<String> getOptimalDateToReviewPackage(long packId);

    @Query("DELETE FROM tasks WHERE id = :taskId AND taskName IS NOT NULL")
    void markAsDone(long taskId);
}
