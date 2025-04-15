package com.example.bc_praca_x.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.bc_praca_x.database.POJO.ActivityWithCategoryAndPackage;
import com.example.bc_praca_x.database.POJO.ActivityWithDetails;
import com.example.bc_praca_x.database.POJO.DailyActivitySummary;
import com.example.bc_praca_x.database.entity.Activity;

import java.util.List;

@Dao
public interface ActivityDao {

    @Insert
    void insert(Activity activity);

    @Query("SELECT SUM(timeSpentInSeconds) FROM activities")
    LiveData<Integer> getTotalTimeSpent();

    @Query("SELECT COALESCE(SUM(timeSpentInSeconds), 0) FROM activities WHERE cardId IN (:categoryIds)")
    LiveData<Integer> getTotalTimeSpentForCategories(List<Long> categoryIds);

    @Query("SELECT strftime('%Y-%m-%d', datetime(date / 1000, 'unixepoch')) AS day, " +
            "COUNT(*) AS count, " +
            "SUM(timeSpentInSeconds) AS totalTime " +
            "FROM activities " +
            "WHERE date BETWEEN :from AND :to " +
            "GROUP BY day " +
            "ORDER BY day ASC")
    LiveData<List<DailyActivitySummary>> getDailyActivitySummary(long from, long to);

    @Query("SELECT SUM(timeSpentInSeconds) FROM activities WHERE date BETWEEN :from AND :to")
    LiveData<Integer> getTotalTimeSpentByWeek(long from, long to);

    @Query("SELECT cp.name AS cardPackName, c.name AS categoryName " +
            "FROM activities a " +
            "JOIN card_packs cp ON a.cardId = cp.id " +
            "JOIN categories c ON cp.card_category_id = c.id " +
            "WHERE a.date BETWEEN :from AND :to " +
            "GROUP BY cp.id " +
            "ORDER BY COUNT(a.id) DESC " +
            "LIMIT 1")
    LiveData<ActivityWithCategoryAndPackage> getFrequentPackageWithCategory(long from, long to);

    @Query("SELECT strftime('%d.%m.%Y', datetime(date / 1000, 'unixepoch')) AS day " +
            "FROM activities " +
            "WHERE cardId = :packageId " +
            "ORDER BY date DESC " +
            "LIMIT 1")
    LiveData<String> getLastTimeLeartDate(long packageId);

    //@Query("SELECT * FROM activities WHERE date BETWEEN :day AND :day + 86400000")
    @Query("SELECT a.*, p.name AS packageName, c.name AS categoryName " +
            "FROM activities a " +
            "INNER JOIN card_packs p ON a.cardId = p.id " +
            "INNER JOIN categories c ON p.card_category_id = c.id " +
            "WHERE a.date BETWEEN :day AND (:day + 86400000) ORDER BY a.date DESC")
    LiveData<List<ActivityWithDetails>> getActivitiesByDay(long day);

    @Query("SELECT SUM(timeSpentInSeconds) FROM activities WHERE cardId = :packageId")
    LiveData<Integer> getPackageTotalTimeSpent(long packageId);
}
