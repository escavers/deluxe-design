package com.example.deluxedesignv42.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ProjectEntity.class, QuoteEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract ProjectDao projectDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "deluxedesign_db")
                            .fallbackToDestructiveMigration() // Permitido temporalmente para desarrollo de fase sin romper la persistencia base
                            .build();
                }
            }
        }
        return instance;
    }
}