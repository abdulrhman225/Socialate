package com.example.postappwithkolin.Room.Post;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface PostDao {
    @Insert
    Completable InsertPost(postTable post);

    @Query("Delete  from post_table")
    Completable deleteFromPostTable();

    @Query("Select*from post_table ")
    Observable<List<postTable>>getAllPosts();
}
