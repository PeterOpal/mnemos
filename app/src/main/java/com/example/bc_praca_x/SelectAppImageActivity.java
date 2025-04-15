package com.example.bc_praca_x;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bc_praca_x.adapters.ImageGridAdapter;
import com.example.bc_praca_x.database.entity.Media;
import com.example.bc_praca_x.database.viewmodel.MediaViewModel;

import java.util.List;

public class SelectAppImageActivity extends ActivitySetup {

    private RecyclerView recyclerView;
    private List<Media> images;
    private MediaViewModel mediaViewModel;
    private String selectedPath;
    private int position, blockPosition;
    private boolean isFront;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_app_image);

        position = getIntent().getIntExtra("position", -1);
        blockPosition = getIntent().getIntExtra("blockPosition", -1);
        isFront = getIntent().getBooleanExtra("isFront", false);

        recyclerView = findViewById(R.id.imageList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);

        loadImages();

        //toolbar
        Toolbar toolbar = findViewById(R.id.addPackTool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

    }

    private void loadImages(){
       mediaViewModel.getAllImages().observe(this, images -> {
           this.images = images;
           ImageGridAdapter adapter = new ImageGridAdapter(this, images, new ImageGridAdapter.OnImageSelectedListener() {
               @Override
               public void onImageSelected(String path, int position) {
                   selectedPath = path;
               }
           });
            recyclerView.setAdapter(adapter);
        });
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
            Intent returnIntent = new Intent();
            returnIntent.putExtra("position", position);
            returnIntent.putExtra("updatedContent", selectedPath);
            returnIntent.putExtra("blockPosition", blockPosition);
            returnIntent.putExtra("isFront", isFront);
            setResult(RESULT_OK, returnIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}