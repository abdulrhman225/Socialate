package com.example.postappwithkolin.Room.Users;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface UserDao {
    @Insert
    Completable InsertUser(UserTable user);

    @Query("Delete from userTable")
    Completable deleteAllDataFromUserTable();

    @Query("Select * from userTable order by id")
    Observable<List<UserTable>> getAllUsers();
}
