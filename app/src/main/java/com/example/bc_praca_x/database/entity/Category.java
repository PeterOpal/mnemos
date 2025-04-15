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
        tableName = "categories",
        foreignKeys = @ForeignKey(
                entity = Media.class,
                parentColumns = "id",
                childColumns = "media_id"
        ),
        indices = {@Index(value = "media_id")}
)
@TypeConverters(DateConverter.class)
public class Category {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String description;
    public Date created_at;
    public Date deleted_at;
    public boolean favorite;
    public boolean deleted;
    public Long media_id;

    public Category(String name, String description, Date created_at, Date deleted_at, boolean favorite, boolean deleted, Long media_id) {
        this.name = name;
        this.description = description;
        this.created_at = created_at;
        this.deleted_at = deleted_at;
        this.favorite = favorite;
        this.deleted = deleted;
        this.media_id = media_id;
    }

    //for saving category
    @Ignore
    public Category(String name, String description, Long media_id){
        this.name=name;
        this.description=description;
        this.media_id=media_id;
        this.created_at=new Date();
        this.deleted_at=null;
        this.favorite=false;
        this.deleted=false;
    }

    //update without media
    @Ignore
    public Category(long id, String name, String description){
        this.id=id;
        this.name=name;
        this.description=description;
        this.created_at=new Date();
        this.deleted_at=null;
        this.favorite=false;
        this.deleted=false;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getMedia_id() {
        return media_id;
    }

    public void setMedia_id(Long media_id) {
        this.media_id = media_id;
    }
}
