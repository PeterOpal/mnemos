package com.example.bc_praca_x.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.bc_praca_x.database.enums.CardSide;
import com.example.bc_praca_x.database.enums.CardType;
import com.example.bc_praca_x.database.type_converter.EnumConverter;
import com.example.bc_praca_x.database.type_converter.JsonConverter;

@Entity(
        tableName = "card_parts",
        foreignKeys = @ForeignKey(
                entity = Card.class,
                parentColumns = "id",
                childColumns = "cardId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "cardId")}
)
@TypeConverters({EnumConverter.class, JsonConverter.class})
public class CardPart {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public CardSide side;
    public CardType type;
    public CardContent cardContent;
    public int orderIndex;
    public long cardId;

    public CardPart(long id, CardSide side, CardType type, CardContent cardContent, int orderIndex, long cardId) {
        this.id = id;
        this.side = side;
        this.type = type;
        this.cardContent = cardContent;
        this.orderIndex = orderIndex;
        this.cardId = cardId;
    }

    @Ignore
    public CardPart(CardSide side, CardType type, CardContent cardContent, int orderIndex, long cardId) {
        this.side = side;
        this.type = type;
        this.cardContent = cardContent;
        this.orderIndex = orderIndex;
        this.cardId = cardId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CardSide getSide() {
        return side;
    }

    public void setSide(CardSide side) {
        this.side = side;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public CardContent getCardContent() {
        return cardContent;
    }

    public void setCardContent(CardContent cardContent) {
        this.cardContent = cardContent;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }
}
