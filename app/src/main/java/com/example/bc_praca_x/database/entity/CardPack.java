package com.example.bc_praca_x.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.bc_praca_x.database.type_converter.DateConverter;

import java.util.Date;

@Entity(
        tableName = "card_packs",
         foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "card_category_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "card_category_id")}
)
@TypeConverters(DateConverter.class)
public class CardPack {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public boolean favourite;
    public boolean deleted;
    public Date created_at;
    public Date deleted_at;
    public long card_category_id;

    public CardPack(String name, boolean favourite, boolean deleted, Date created_at, Date deleted_at, long card_category_id) {
        this.name = name;
        this.favourite = favourite;
        this.deleted = deleted;
        this.created_at = created_at;
        this.deleted_at = deleted_at;
        this.card_category_id = card_category_id;
    }

    //add pack from activity
    @Ignore
    public CardPack(String name, long card_category_id){
        this.name = name;
        this.card_category_id = card_category_id;
        this.favourite = false;
        this.deleted = false;
        this.created_at = new Date();
        this.deleted_at = null;
    }

    //update pack from activity
    @Ignore
    public CardPack(long packId, String name, long card_category_id){
        this.id = packId;
        this.name = name;
        this.card_category_id = card_category_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFavourite() { return favourite; }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Date deleted_at) {
        this.deleted_at = deleted_at;
    }

    public long getCategoryId() {
        return card_category_id;
    }

    public void sedCategoryId(long card_category_id) {
        this.card_category_id = card_category_id;
    }
}
