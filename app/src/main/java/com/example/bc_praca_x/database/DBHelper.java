package com.example.bc_praca_x.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.bc_praca_x.database.dao.ActivityDao;
import com.example.bc_praca_x.database.dao.CardDao;
import com.example.bc_praca_x.database.dao.CardPackDao;
import com.example.bc_praca_x.database.dao.CardPartDao;
import com.example.bc_praca_x.database.dao.CategoryDao;
import com.example.bc_praca_x.database.dao.MediaDao;
import com.example.bc_praca_x.database.dao.TaskDao;
import com.example.bc_praca_x.database.entity.Activity;
import com.example.bc_praca_x.database.entity.Card;
import com.example.bc_praca_x.database.entity.CardPack;
import com.example.bc_praca_x.database.entity.CardPart;
import com.example.bc_praca_x.database.entity.Category;
import com.example.bc_praca_x.database.entity.Media;
import com.example.bc_praca_x.database.entity.Task;

@Database(entities = {Category.class, CardPack.class, Card.class, CardPart.class, Media.class, Task.class, Activity.class}, version = 27)
public abstract class DBHelper extends RoomDatabase {

    private static volatile DBHelper INSTANCE;
    public abstract CategoryDao categoryDao();
    public abstract CardPackDao cardPackDao();
    public abstract CardDao cardDao();
    public abstract CardPartDao cardPartDao();
    public abstract MediaDao mediaDao();
    public abstract TaskDao taskDao();
    public abstract ActivityDao activityDao();

    public static DBHelper getInstance(Context context){
        if(INSTANCE == null){
            synchronized (DBHelper.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DBHelper.class, "flashcards_database")
                            .fallbackToDestructiveMigration()
                            .setJournalMode(JournalMode.TRUNCATE)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
