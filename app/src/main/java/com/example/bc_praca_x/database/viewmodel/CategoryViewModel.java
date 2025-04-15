package com.example.bc_praca_x.database.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.bc_praca_x.database.POJO.CategoryWithImage;
import com.example.bc_praca_x.database.entity.Category;
import com.example.bc_praca_x.database.repository.CategoryRepository;
import com.example.bc_praca_x.helpers.ItemInsertedToDB;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepository repository;
    private final LiveData<List<Category>> allCategories;

    public CategoryViewModel(Application application) {
        super(application);
        repository = new CategoryRepository(application);
        allCategories = repository.getActiveCategories();
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void insert(Category category) {
        repository.insert(category);
    }

    public void insertAndGetId(Category category, ItemInsertedToDB callback) {
        repository.insertAndGetId(category, callback);
    }

    public LiveData<Integer> getActiveCategoryCount() {
        return repository.getActiveCategoryCount();
    }

    public LiveData<List<CategoryWithImage>> getActiveCategoriesWithImage() {
        return repository.getActiveCategoriesWithImage();
    }

    public void delete(long id) {
        repository.delete(id);
    }

    public void update(Category category) {
        repository.update(category);
    }

    public LiveData<Category> getCategoryById(long id) {
        return repository.getCategoryById(id);
    }

    public void setCategoryFavourite(long categoryId, boolean isFavorite) {
        repository.setCategoryFavourite(categoryId, isFavorite);
    }

    public void deleteAll(){
        repository.deleteAll();
    }
}