package com.example.bc_praca_x.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.bc_praca_x.database.DBHelper;
import com.example.bc_praca_x.database.dao.CardPartDao;
import com.example.bc_praca_x.database.entity.CardPart;
import com.example.bc_praca_x.database.enums.CardSide;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CardPartRepository {

    private final CardPartDao cardPartDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CardPartRepository(Application application) {
        DBHelper db = DBHelper.getInstance(application);
        this.cardPartDao = db.cardPartDao();
    }

    public LiveData<List<CardPart>> getCardPartsByCardId(long id) {
        return cardPartDao.getCardPartsByCardId(id);
    }

    public void insert(CardPart cardPart) {
        executorService.execute(() -> cardPartDao.insert(cardPart));
    }

    public void update(CardPart cardPart) {
        executorService.execute(() -> cardPartDao.update(cardPart));
    }

    public void deleteCardPartsForCard(long cardId, CardSide side) {
        executorService.execute(() -> cardPartDao.deleteCardPartsForCard(cardId, side));
    }

    public LiveData<List<CardPart>> getCardPartsForCard(long cardId, CardSide type) {
        return cardPartDao.getCardPartsForCard(cardId, type);
    }

    public void delete(CardPart cardPart) {
        executorService.execute(() -> cardPartDao.delete(cardPart));
    }
}
