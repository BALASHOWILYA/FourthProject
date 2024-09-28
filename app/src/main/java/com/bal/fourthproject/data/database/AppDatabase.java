package com.bal.fourthproject.data.database;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;


@Database(entities = {CharacterEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CharacterDao characterDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "character_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
