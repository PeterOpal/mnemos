package com.example.bc_praca_x.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageSaver { //async task

    private Context context;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public ImageSaver(Context context) {
        this.context = context;
    }

    public void saveImage(Uri path, ImageSaveCallback callback) {
        executor.execute(() -> {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), path);
                File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (!directory.exists()) directory.mkdirs();

                String fileName = UUID.randomUUID().toString() + ".jpg";
                File imageFile = new File(directory, fileName);

                FileOutputStream fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fos);
                fos.close();

                callback.onImageSaved(imageFile.getAbsolutePath());

            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void saveDrawable(int drawableResId, ImageSaveCallback callback) {
        executor.execute(() -> {
            try {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
                File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (directory != null && !directory.exists()) directory.mkdirs();

                String fileName = UUID.randomUUID().toString() + ".jpg";
                File imageFile = new File(directory, fileName);

                FileOutputStream fos = new FileOutputStream(imageFile);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fos);
                fos.close();

                callback.onImageSaved(imageFile.getAbsolutePath());
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void deleteAllSavedImages() {
        File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (directory != null && directory.exists()) {
            File[] files = directory.listFiles();
            Log.d("ImageDelete", "Deleting files in: " + directory.getAbsolutePath());
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        boolean deleted = file.delete();
                        Log.d("ImageDelete", "Deleted: " + file.getName() + " = " + deleted);
                    }
                }
            }
        } else {
            Log.d("ImageDelete", "Directory does not exist");
        }
    }


    public interface ImageSaveCallback {
        void onImageSaved(String path);
        void onError(Exception e);
    }

}
