package com.example.bc_praca_x.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bc_praca_x.database.entity.CardPart;
import com.example.bc_praca_x.database.enums.CardSide;
import com.example.bc_praca_x.database.enums.CardType;

import java.util.List;

@Dao
public interface CardPartDao {

    @Query("SELECT * FROM card_parts WHERE cardId = :id")
    LiveData<List<CardPart>> getCardPartsByCardId(long id);

    @Insert
    void insert(CardPart cardPart);

    @Update
    void update(CardPart cardPart);

    @Query("SELECT * FROM card_parts WHERE cardId = :cardId AND side = :type")
    LiveData<List<CardPart>> getCardPartsForCard(long cardId, CardSide type);

    @Delete
    void delete(CardPart cardPart);

    @Query("DELETE FROM card_parts WHERE cardId = :cardId AND side = :side")
    void deleteCardPartsForCard(long cardId, CardSide side);

    @Query("SELECT * FROM card_parts WHERE type = :type")
    List<CardPart> getCardPartsByTypeSync(CardType type);
}
