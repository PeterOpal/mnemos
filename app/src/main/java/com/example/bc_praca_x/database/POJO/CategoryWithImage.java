package com.example.bc_praca_x.database.POJO;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.bc_praca_x.database.entity.Category;
import com.example.bc_praca_x.database.entity.Media;


public class CategoryWithImage {

    @Embedded
    public Category category;

    @Relation(parentColumn = "media_id", entityColumn = "id")
    public Media media;

}
