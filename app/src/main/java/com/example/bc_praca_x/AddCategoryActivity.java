package com.example.bc_praca_x;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.bc_praca_x.database.entity.Category;
import com.example.bc_praca_x.database.viewmodel.CategoryViewModel;
import com.example.bc_praca_x.database.viewmodel.MediaViewModel;
import com.example.bc_praca_x.helpers.ImagePicker;
import com.example.bc_praca_x.helpers.ImagePickerFromGallery;
import com.example.bc_praca_x.helpers.ImageSaver;

public class AddCategoryActivity extends ActivitySetup {
    private EditText categoryName, categoryDescription;
    private CategoryViewModel categoryViewModel;
    private MediaViewModel mediaViewModel;
    private ImageView categoryImage;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;
    private Uri selectedImageFilePath;
    private ImageSaver imageSaver;
    //UPDATE
    private long categoryId;
    private String categoryNameUpdate;
    private String categoryDescriptionUpdate;
    private String categoryImageUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        categoryName = findViewById(R.id.categoryName);
        categoryDescription = findViewById(R.id.categoryDescription);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);
        categoryImage = findViewById(R.id.categoryImage);
        imageSaver = new ImageSaver(this);

        pickMediaLauncher = ImagePickerFromGallery.createImagePicker(this, new ImagePicker() {
            @Override
            public void onImagePicked(Uri imageUri) {
                selectedImageFilePath = imageUri;
                ImagePickerFromGallery.loadImage(AddCategoryActivity.this, imageUri, categoryImage);
                categoryImage.setVisibility(ImageView.VISIBLE);
            }
        });

        Toolbar toolbar = findViewById(R.id.addCategoryToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        Button btnSelectFile = findViewById(R.id.selectFileButton);
        btnSelectFile.setOnClickListener(v -> openFileSelector());

        //check if we are updating
        checkIsUpdating();
    }

    private void openFileSelector() {
        pickMediaLauncher.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_category_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            if (validateCategory()) saveCategory();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveCategory() {
        if (categoryId != -1) { //update
            categoryViewModel.getCategoryById(categoryId).observe(this, category -> {
                if (category != null) {
                    category.setName(categoryName.getText().toString());
                    category.setDescription(categoryDescription.getText().toString());
                    categoryViewModel.update(category);
                    if(selectedImageFilePath != null) saveNewImageWithCategory(category);
                }
            });
        } else {//create
                Category category = new Category(categoryName.getText().toString(), categoryDescription.getText().toString(), null);
                if(selectedImageFilePath != null) saveNewImageWithCategory(category);
                else  categoryViewModel.insert(category);
        }

        finish();
        overridePendingTransition(0, 0);
    }

    private boolean validateCategory() {
        if (categoryName.getText().toString().isEmpty()) {
            categoryName.setError(getString(R.string.required));
            categoryName.requestFocus();
            return false;
        }

        return true;
    }


    private void checkIsUpdating() {
        Intent intent = getIntent();
        categoryId = intent.getLongExtra("category_id", -1);
        categoryNameUpdate = intent.getStringExtra("category_name");
        categoryDescriptionUpdate = intent.getStringExtra("category_description");
        categoryImageUpdate = intent.getStringExtra("category_image");

        if (categoryId != -1) {
            Toolbar toolbar = findViewById(R.id.addCategoryToolbar);
            toolbar.setTitle(getString(R.string.update_category));


            categoryName.setText(categoryNameUpdate);
            categoryDescription.setText(categoryDescriptionUpdate);
            if (categoryImageUpdate != null) {
                Glide.with(this).load(categoryImageUpdate).into(categoryImage);
                categoryImage.setVisibility(ImageView.VISIBLE);
            }
        }
    }

    public void saveNewImageWithCategory(Category category) {

        imageSaver.saveImage(selectedImageFilePath, new ImageSaver.ImageSaveCallback() {
            @Override
            public void onImageSaved(String path) {
                //THE IMAGE IS SAVED ON DISK, NOW SAVE THE PATH TO THE DATABASE
                mediaViewModel.insertUserMedia(path, mediaId -> {
                    category.setMedia_id(mediaId);
                    if(categoryId != -1) categoryViewModel.update(category);
                    else categoryViewModel.insert(category);
                });
            }

            @Override
            public void onError(Exception e) {
                Log.d("IMAGE", "onError: " + e.getMessage());
            }
        });
    }
}