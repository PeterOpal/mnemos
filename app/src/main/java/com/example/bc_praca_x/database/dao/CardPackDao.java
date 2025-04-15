package com.example.bc_praca_x.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.bc_praca_x.database.entity.CardPack;
import java.util.List;

@Dao
public interface CardPackDao {

    @Query("SELECT * FROM card_packs")
    LiveData<List<CardPack>> getActiveCardPacks();

    @Query("SELECT * FROM card_packs WHERE card_category_id = :id ORDER BY created_at DESC")
    LiveData<List<CardPack>> getCardPacksByCategoryId(long id);

    @Query("SELECT COUNT(*) FROM card_packs")
    LiveData<Integer> getActiveCardPackCount();

    @Query("SELECT COUNT(*) FROM card_packs WHERE card_category_id = :id")
    LiveData<Integer> getActiveCardPackCountByCategoryId(long id);

    @Query("UPDATE card_packs SET favourite = :isFavorite WHERE id = :packageId")
    void setPackageFavorite(long packageId, boolean isFavorite);

    @Transaction
    default void deleteCardPackWithTasks(long cardPackId) {
        deleteTasksForCardPack(cardPackId);
        deleteCardPack(cardPackId);
    }

    @Query("DELETE FROM tasks WHERE card_packs_id = :cardPackId")
    void deleteTasksForCardPack(long cardPackId);

    @Query("DELETE FROM card_packs WHERE id = :cardPackId")
    void deleteCardPack(long cardPackId);

    @Insert
    void insert(CardPack cardPack);

    @Insert
    long insertAndGetId(CardPack cardPack);

    @Update
    void updateCardPack(CardPack cardPack);
}
