package com.example.bc_praca_x.database.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.bc_praca_x.database.entity.Media;
import com.example.bc_praca_x.database.repository.MediaRepository;

import java.util.List;

public class MediaViewModel extends AndroidViewModel {

    private final MediaRepository repository;


    public MediaViewModel(Application application) {
        super(application);
        repository = new MediaRepository(application);
    }

    public void insertUserMedia(String path, MediaRepository.InsertCallback callback) {
        repository.insertUserMedia(path, callback);
    }

    public LiveData<Media> getMedia(long id) {
        return repository.getMediaById(id);
    }

    public LiveData<List<Media>> getAllImages() {
        return repository.getAllImages();
    }

    public LiveData<Media> existingMedia(String path) {
        return repository.existingMedia(path);
    }

    public void deleteAllMedia() {
        repository.deleteAllMedia();
    }

}
