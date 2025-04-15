package com.example.bc_praca_x.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.bc_praca_x.database.POJO.CardWithParts;
import com.example.bc_praca_x.database.entity.Card;

import java.util.List;

@Dao
public interface CardDao {
    @Query("SELECT * FROM cards WHERE cardPackId = :id")
    LiveData<List<Card>> getCardsByPackId(long id);

    @Insert
    long insert(Card card);

    // queries for cardpackage
    @Query("SELECT EXISTS (SELECT 1 FROM cards WHERE cardPackId = :id LIMIT 1)")
    LiveData<Boolean> doesCardPackHaveCards(long id);

    @Query("SELECT COUNT(*) FROM cards WHERE cardPackId = :cardPackId")
    LiveData<Integer> getCardCountByPackId(long cardPackId);

    // queries for card overview
    @Query("SELECT COUNT(*) FROM cards WHERE DATE(nextReviewDate / 1000, 'unixepoch') <= DATE('now') AND cardPackId = :cardPackId")
    //@Query("SELECT COUNT(*) FROM cards WHERE nextReviewDate / 1000 <= strftime('%s', 'now') AND cardPackId = :cardPackId")
    LiveData<Integer> getReviewableCardsCountByPackId(long cardPackId);

    // queries for card presenter
    @Transaction
    @Query("SELECT * FROM cards WHERE cardPackId = :cardPackId ")
    LiveData<List<CardWithParts>> getCardsWithPartsByPackId(long cardPackId);
   /* @Transaction
    @Query("SELECT Card.*, CardPart.*, Media.* FROM cards Card JOIN card_parts CardPart ON Card.id = CardPart.cardId JOIN media Media ON CardPart.cardContent = Media.id WHERE Card.cardPackId = :cardPackId")
    LiveData<List<CardWithParts>> getCardsWithPartsByPackId(long cardPackId);*/

    @Transaction
    @Query("SELECT * FROM cards WHERE DATE(nextReviewDate / 1000, 'unixepoch') <= DATE('now') AND cardPackId = :cardPackId")
    LiveData<List<CardWithParts>> getSMCardsWithPartsByPackId(long cardPackId);

    @Query("SELECT * FROM cards WHERE id = :id LIMIT 1")
    LiveData<Card> getCardById(long id);

    @Update
    void update(Card card);

    @Query("DELETE FROM cards WHERE id = :id")
    void delete(long id);


}