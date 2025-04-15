package com.example.bc_praca_x.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.bc_praca_x.database.POJO.CategoryWithImage;
import com.example.bc_praca_x.database.entity.Category;
import java.util.List;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getActiveCategories();

    @Query("SELECT * FROM categories WHERE id = :id")
    Category getCategoryByIdSync(long id);

    @Transaction
    @Query("SELECT * FROM categories ORDER BY created_at DESC")
    LiveData<List<CategoryWithImage>> getActiveCategoriesWithImage();

    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<Category> getCategoryById(long id);

    @Insert
    void insert(Category category);
    @Insert
    long insertAndGetId(Category category);

    @Query("SELECT COUNT(*) FROM categories WHERE deleted = 0")
    LiveData<Integer> getActiveCategoryCount();

    @Transaction
    @Delete
    void delete(Category category);

    @Update
    void update(Category category);

    @Query("UPDATE categories SET favorite = :favorite WHERE id = :id")
    void setCategoryFavorite(long id, boolean favorite);

    @Query("DELETE FROM categories")
    void deleteAll();

    @Query("SELECT * FROM categories WHERE media_id IS NOT NULL")
    List<Category> getAllCategoriesSync();
}
