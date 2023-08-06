package com.example.postappwithkolin.Room.Users;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserTable.class} , version = 1)
public abstract class UsersDataBase extends RoomDatabase {
    public abstract UserDao userDao();
    private static UsersDataBase Instance;

    public static synchronized UsersDataBase getInstance(Context context){
        if (Instance == null){
            Instance = Room.databaseBuilder(context.getApplicationContext() , UsersDataBase.class , "userDataBase")
                    .fallbackToDestructiveMigration().build();
        }
        return Instance;
    }

}
