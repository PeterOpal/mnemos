package com.example.bc_praca_x.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.bc_praca_x.database.entity.Media;

import java.util.List;

@Dao
public interface MediaDao {

    @Query("SELECT * FROM media WHERE id = :id")
    LiveData<Media> getMediaById(long id);

    @Insert
    long insertUserMedia(Media media);

    @Query("SELECT * FROM media")
    LiveData<List<Media>> getAllImages();

    @Query("SELECT * FROM media")
    List<Media> getAllImagesSync();

    @Query("SELECT * FROM media WHERE path = :path LIMIT 1")
    LiveData<Media> existingMedia(String path);

    @Query("DELETE FROM media")
    void deleteAllMedia();

    @Query("DELETE FROM media WHERE id = :id")
    void deleteMediaById(long id);
}
