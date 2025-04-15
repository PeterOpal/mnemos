package com.example.bc_praca_x.database.type_converter;

import androidx.room.TypeConverter;

import com.example.bc_praca_x.database.enums.CardSide;
import com.example.bc_praca_x.database.enums.CardType;

public class EnumConverter {

    //CARD TYPE
    @TypeConverter
    public static String fromContentType(CardType contentType) {
        return contentType == null ? null : contentType.name();
    }

    @TypeConverter
    public static CardType toContentType(String contentType) {
        return contentType == null ? null : CardType.valueOf(contentType);
    }

    //CARD SIDE
    @TypeConverter
    public static String fromCardSide(CardSide cardSide) {
        return cardSide == null ? null : cardSide.name();
    }

    @TypeConverter
    public static CardSide toCardSide(String cardSide) {
        return cardSide == null ? null : CardSide.valueOf(cardSide);
    }
}
