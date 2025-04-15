package com.example.bc_praca_x.helpers;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ImagePickerFromGallery {

    public static ActivityResultLauncher<PickVisualMediaRequest> createImagePicker(Context context, ImagePicker callback) {
        return ((AppCompatActivity) context).registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        callback.onImagePicked(uri);
                    }
                });
    }

    public static void loadImage(Context context, Uri uri, ImageView imageView) {
        Glide.with(context)
                .load(uri)
                .into(imageView);
    }
}
