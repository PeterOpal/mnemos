package com.example.bc_praca_x.database.POJO;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.bc_praca_x.database.entity.CardPack;
import com.example.bc_praca_x.database.entity.Category;
import com.example.bc_praca_x.database.entity.Task;

public class TaskWithPack {
    @Embedded
    public Task task;

    @Relation(parentColumn = "card_packs_id", entityColumn = "id")
    public CardPack cardPack;

    //specialna relacia, pozri DAO vrstvu
    @Relation(parentColumn = "id", entityColumn = "id")
    public Category category;
}
