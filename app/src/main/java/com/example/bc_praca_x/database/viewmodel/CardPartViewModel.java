package com.example.bc_praca_x.database.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.bc_praca_x.database.entity.CardPart;
import com.example.bc_praca_x.database.enums.CardSide;
import com.example.bc_praca_x.database.repository.CardPartRepository;

import java.util.List;

public class CardPartViewModel extends AndroidViewModel {

    private final CardPartRepository repository;

    public CardPartViewModel(Application application) {
        super(application);
        repository = new CardPartRepository(application);
    }

    public LiveData<List<CardPart>> getCardPartsByCardId(long id) {
        return repository.getCardPartsByCardId(id);
    }

    public void insert(CardPart cardPart) {
        repository.insert(cardPart);
    }

    public void update(CardPart cardPart) {
        repository.update(cardPart);
    }

    public LiveData<List<CardPart>> getCardPartsForCard(long cardId, CardSide type) {
        return repository.getCardPartsForCard(cardId, type);
    }

    public void delete(CardPart cardPart) {
        repository.delete(cardPart);
    }

    public void deleteCardPartsForCard(long cardId, CardSide side) {
        repository.deleteCardPartsForCard(cardId, side);
    }
}
