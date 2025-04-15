package com.example.bc_praca_x.database.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.bc_praca_x.database.type_converter.DateConverter;

import java.util.Date;

@Entity(tableName = "media")
@TypeConverters(DateConverter.class)
public class Media {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String path;
    public Date created_at;

    public Media(long id, String path, Date created_at) {
        this.id = id;
        this.path = path;
        this.created_at = created_at;
    }

    @Ignore
    public Media(String path, Date created_at) {
        this.path = path;
        this.created_at = created_at;
    }

    @Ignore
    public Media(String path) {
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
