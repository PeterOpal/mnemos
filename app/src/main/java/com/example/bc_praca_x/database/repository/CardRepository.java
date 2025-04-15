package com.example.bc_praca_x.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.bc_praca_x.database.DBHelper;
import com.example.bc_praca_x.database.POJO.CardWithParts;
import com.example.bc_praca_x.database.dao.CardDao;
import com.example.bc_praca_x.database.entity.Card;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CardRepository {

    private final CardDao cardDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CardRepository(Application application) {
        DBHelper database = DBHelper.getInstance(application);
        this.cardDao = database.cardDao();
    }

    public LiveData<List<Card>> getCardsByPackId(long id) {
        return cardDao.getCardsByPackId(id);
    }

    public void insert(Card card, OnCardInsertedListener listener) {
        executorService.execute(() -> {
            long cardId = cardDao.insert(card);
            listener.onCardInserted(cardId);
        });
    }

    public interface OnCardInsertedListener {
        void onCardInserted(long cardId);
    }

    public LiveData<Boolean> doesCardPackHaveCards(long id) {
        return cardDao.doesCardPackHaveCards(id);
    }

    public LiveData<Integer> getCardCount(long cardPackId) {
        return cardDao.getCardCountByPackId(cardPackId);
    }

    public LiveData<List<CardWithParts>> getCardsWithPartsByPackId(long cardPackId) {
        return cardDao.getCardsWithPartsByPackId(cardPackId);
    }

    public LiveData<Card> getCardById(long id) {
        return cardDao.getCardById(id);
    }

    public void update(Card card) {
        executorService.execute(() -> cardDao.update(card));
    }

    public LiveData<Integer> getReviewableCardsCountByPackId(long id) {
        return cardDao.getReviewableCardsCountByPackId(id);
    }

    public LiveData<List<CardWithParts>> getSMCardsWithPartsByPackId(long cardPackId) {
        return cardDao.getSMCardsWithPartsByPackId(cardPackId);
    }

    public void delete(long id) {
        executorService.execute(() -> cardDao.delete(id));
    }
}
