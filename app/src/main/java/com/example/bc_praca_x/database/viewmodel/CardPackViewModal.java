package com.example.bc_praca_x.database.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.bc_praca_x.database.entity.CardPack;
import com.example.bc_praca_x.database.entity.Category;
import com.example.bc_praca_x.database.repository.CardPackRepository;
import com.example.bc_praca_x.database.repository.CategoryRepository;
import com.example.bc_praca_x.helpers.ItemInsertedToDB;

import java.util.List;

public class CardPackViewModal extends AndroidViewModel {

    private final CardPackRepository repository;
    private final LiveData<List<CardPack>> allCardPacks;

    public CardPackViewModal(Application application) {
        super(application);
        repository = new CardPackRepository(application);
        allCardPacks = repository.getActiveCategories();
    }

    public LiveData<List<CardPack>> getAllCardPacks() { return allCardPacks; }

    public void insert(CardPack cardPack) {
        repository.insert(cardPack);
    }

    public void insertAndGetId(CardPack cardPack, ItemInsertedToDB callback) {
        repository.insertAndGetId(cardPack, callback);
    }

    public LiveData<List<CardPack>> getCardPacksByCategoryId(long id) {
        return repository.getCardPacksByCategoryId(id);
    }

    public LiveData<Integer> getActiveCardPackCount() {
        return repository.getActiveCardPackCount();
    }

    public LiveData<Integer> getActiveCardPackCountByCategoryId(long id) {
        return repository.getActiveCardPackCountByCategoryId(id);
    }

    public void setPackageFavorite(long categoryId, boolean isFavorite) {
        repository.setPackageFavorite(categoryId, isFavorite);
    }

    public void deleteCardPackWithTasks(long id) {
        repository.deleteCardPackWithTasks(id);
    }

    public void updateCardPack(CardPack cardPack) {
        repository.updateCardPack(cardPack);
    }
}
