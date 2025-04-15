package com.example.bc_praca_x.database.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.bc_praca_x.database.DBHelper;
import com.example.bc_praca_x.database.POJO.CategoryWithImage;
import com.example.bc_praca_x.database.dao.CategoryDao;
import com.example.bc_praca_x.database.entity.Category;
import com.example.bc_praca_x.helpers.ItemInsertedToDB;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryRepository {
    private final CategoryDao categoryDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CategoryRepository(Application application) {
        DBHelper database = DBHelper.getInstance(application);
        categoryDao = database.categoryDao();
    }

    public void insert(Category category) {
        executorService.execute(() -> categoryDao.insert(category));
    }

    public void insertAndGetId(Category category, ItemInsertedToDB callback) {
        executorService.execute(() -> {
            long id = categoryDao.insertAndGetId(category);
            callback.onItemInserted(id);
        });
    }

    public LiveData<List<Category>> getActiveCategories() {
        return categoryDao.getActiveCategories();
    }

    public LiveData<Integer> getActiveCategoryCount() {
        return categoryDao.getActiveCategoryCount();
    }

    public LiveData<List<CategoryWithImage>> getActiveCategoriesWithImage() {
        return categoryDao.getActiveCategoriesWithImage();
    }

    public void delete(long id) {
        executorService.execute(() -> {
            Category category = categoryDao.getCategoryByIdSync(id);
            if (category != null) {
                categoryDao.delete(category);
            }
        });
    }


    public void update(Category category) {
        executorService.execute(() -> categoryDao.update(category));
    }

    public LiveData<Category> getCategoryById(long id) {
        return categoryDao.getCategoryById(id);
    }

    public void setCategoryFavourite(long categoryId, boolean isFavorite) {
        executorService.execute(() -> categoryDao.setCategoryFavorite(categoryId, isFavorite));
    }

    public void deleteAll() {
        executorService.execute(categoryDao::deleteAll);
    }

}
