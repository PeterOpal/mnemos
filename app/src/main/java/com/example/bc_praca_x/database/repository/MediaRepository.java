package com.example.bc_praca_x.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.bc_praca_x.database.DBHelper;
import com.example.bc_praca_x.database.dao.MediaDao;
import com.example.bc_praca_x.database.entity.Media;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;

public class MediaRepository {

    public final MediaDao mediaDao;
    public final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public MediaRepository(Application application) {
        DBHelper database = DBHelper.getInstance(application);
        mediaDao = database.mediaDao();
    }


    public LiveData<Media> getMediaById(long id) {
        return mediaDao.getMediaById(id);
    }

    public void insertUserMedia(String path, InsertCallback callback) {
        Media media = new Media(path, new Date());
        executorService.execute(() -> {
            long mediaId = mediaDao.insertUserMedia(media);
            callback.onInsert(mediaId);
        });
    }

    public LiveData<List<Media>> getAllImages() {
        return mediaDao.getAllImages();
    }

    public LiveData<Media> existingMedia(String path) {
        return mediaDao.existingMedia(path);
    }

    public interface InsertCallback {
        void onInsert(long mediaId);
    }

    public void deleteAllMedia() {
        executorService.execute(() -> mediaDao.deleteAllMedia());
    }
}
