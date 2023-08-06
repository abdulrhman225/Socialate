package com.example.postappwithkolin.Room.Post;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.postappwithkolin.Model.UserPost;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class resultPosts extends ViewModel {
    public final MutableLiveData<ArrayList<UserPost>> mutable = new MutableLiveData<ArrayList<UserPost>>();

    public static String TAG = "resultPosts";
    Context context;
    public resultPosts(Context context) {
        this.context = context;
    }

    public void delete() {
        postDataBase postsDB = postDataBase.getInstance(context);
        postsDB.postDao().deleteFromPostTable().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }
                });
    }

    public void Insert(List<UserPost> bosts) {
        postDataBase postsDB = postDataBase.getInstance(context);
        ArrayList<postTable> posts = new ArrayList();

        for (UserPost post : bosts) {
            posts.add(new postTable(post.getUserPhoto() , post.getPostImage() , post.getUserName()  , post.getPostComment()));
        }

        for (postTable post : posts) {
            postsDB.postDao().InsertPost(post).subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onComplete() {
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                        }
                    });
        }
    }

    public void getAllPosts() {
        postDataBase postsDB = postDataBase.getInstance(context);
        postsDB.postDao().getAllPosts().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<postTable>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<postTable> postTables) {

                        if (!postTables.isEmpty()) {
                            ArrayList<UserPost> posts = new ArrayList();

                            for (postTable post : postTables) {
                                posts.add(new UserPost(post.getUserName() , post.getPostName() , post.getPostImage() , post.getUserImage()));
                                mutable.setValue(posts);
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}
