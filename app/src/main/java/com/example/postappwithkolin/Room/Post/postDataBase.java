package com.example.postappwithkolin.Room.Post;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {postTable.class} , version = 8)
public abstract class postDataBase extends RoomDatabase {
    public abstract PostDao postDao();
    private static postDataBase Instance;

    public static synchronized postDataBase getInstance(Context context){
        if (Instance == null){
            Instance = Room.databaseBuilder(context.getApplicationContext() , postDataBase.class , "postDataBase")
                    .fallbackToDestructiveMigration().build();
        }
        return Instance;
    }
}
