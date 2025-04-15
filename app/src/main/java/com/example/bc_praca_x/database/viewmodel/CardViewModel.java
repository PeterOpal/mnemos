package com.example.bc_praca_x.database.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.bc_praca_x.database.POJO.CardWithParts;
import com.example.bc_praca_x.database.entity.Card;
import com.example.bc_praca_x.database.repository.CardRepository;

import java.util.List;

public class CardViewModel extends AndroidViewModel {

    private final CardRepository repository;

    public CardViewModel(Application application) {
        super(application);
        repository = new CardRepository(application);
    }

    public LiveData<List<Card>> getCardsByCardPackId(long id) {
        return repository.getCardsByPackId(id);
    }

    public LiveData<Card> getCardById(long id) {
        return repository.getCardById(id);
    }

    public void insert(Card card, CardRepository.OnCardInsertedListener listener) {
        repository.insert(card, listener);
    }

    public LiveData<Boolean> doesCardPackHaveCards(long id) {
        return repository.doesCardPackHaveCards(id);
    }

    public LiveData<Integer> getCardCount(long cardPackId) {
        return repository.getCardCount(cardPackId);
    }

    public LiveData<List<CardWithParts>> getCardsWithPartsByPackId(long cardPackId, boolean algorithm) {
        if(algorithm) return repository.getSMCardsWithPartsByPackId(cardPackId);
        else return repository.getCardsWithPartsByPackId(cardPackId);
    }


    public void update(Card card) {
        repository.update(card);
    }

    public void delete(long id) {
        repository.delete(id);
    }

    public LiveData<Integer> getReviewableCardsCountByPackId(long id) {
        return repository.getReviewableCardsCountByPackId(id);
    }
}
