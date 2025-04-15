package com.example.bc_praca_x.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.bc_praca_x.database.DBHelper;
import com.example.bc_praca_x.database.dao.CardPackDao;
import com.example.bc_praca_x.database.entity.CardPack;
import com.example.bc_praca_x.helpers.ItemInsertedToDB;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CardPackRepository {
    private final CardPackDao cardPackDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CardPackRepository(Application application) {
        DBHelper database = DBHelper.getInstance(application);
        cardPackDao = database.cardPackDao();
    }

    public LiveData<List<CardPack>> getActiveCategories() {
        return cardPackDao.getActiveCardPacks();
    }

    public void insert(CardPack cardPack) {
        executorService.execute(() -> cardPackDao.insert(cardPack));
    }

    public void insertAndGetId(CardPack cardPack, ItemInsertedToDB callback) {
        executorService.execute(() -> {
            long id = cardPackDao.insertAndGetId(cardPack);
            callback.onItemInserted(id);
        });
    }

    public LiveData<List<CardPack>> getCardPacksByCategoryId(long id) {
        return cardPackDao.getCardPacksByCategoryId(id);
    }

    public LiveData<Integer> getActiveCardPackCount() {
        return cardPackDao.getActiveCardPackCount();
    }

    public LiveData<Integer> getActiveCardPackCountByCategoryId(long id) {
        return cardPackDao.getActiveCardPackCountByCategoryId(id);
    }

    public void setPackageFavorite(long categoryId, boolean isFavorite) {
        executorService.execute(() -> cardPackDao.setPackageFavorite(categoryId, isFavorite));
    }

    public void deleteCardPackWithTasks(long id) {
        executorService.execute(() -> cardPackDao.deleteCardPackWithTasks(id));
    }

    public void updateCardPack(CardPack cardPack) {
        executorService.execute(() -> cardPackDao.updateCardPack(cardPack));
    }
}
