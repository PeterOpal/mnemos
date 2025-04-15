package com.example.bc_praca_x.database.POJO;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.bc_praca_x.database.entity.Card;
import com.example.bc_praca_x.database.entity.CardPart;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CardWithParts {

    @Embedded
    public Card card;

    @Relation(parentColumn = "id", entityColumn = "cardId")
    public List<CardPart> cardParts;

    public List<CardPart> getSortedCardParts() {
        if (cardParts != null) Collections.sort(cardParts, Comparator.comparingInt(CardPart::getOrderIndex));
        return cardParts;
    }

}
