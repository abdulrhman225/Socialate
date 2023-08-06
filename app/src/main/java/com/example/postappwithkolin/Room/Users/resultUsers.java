package com.example.postappwithkolin.Room.Users;

import android.content.Context;

import com.example.postappwithkolin.Model.UserInformation;
import com.example.postappwithkolin.Model.users;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class resultUsers {
        public static List<UserInformation> AllUsers = new ArrayList<>();
        Context context;
        public resultUsers(Context context) {
            this.context = context;
        }

        public void delete() {
             UsersDataBase userDB = UsersDataBase.getInstance(context);
            userDB.userDao().deleteAllDataFromUserTable().subscribeOn(Schedulers.computation())
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

        public void Insert(List<UserInformation> Users) {
            UsersDataBase userDB = UsersDataBase.getInstance(context);
            ArrayList<UserTable> users = new ArrayList();

            for (UserInformation user : Users) {
                users.add(new UserTable(user.getUserName() , user.getEmail() ,user.getUserPhoto()));
            }

            for (UserTable user : users) {
                userDB.userDao().InsertUser(user).subscribeOn(Schedulers.computation())
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

        public void getAllUsers() {
            UsersDataBase userDB = UsersDataBase.getInstance(context);
            userDB.userDao().getAllUsers().subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<UserTable>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull List<UserTable> userTables) {
                            if (!userTables.isEmpty()) {
                                ArrayList<UserInformation> users = new ArrayList();

                                for (UserTable user : userTables) {
                                    users.add(new UserInformation(user.getUserName() , user.getEmail() , user.getUserPhoto()));
                                }
                                AllUsers.clear();
                                AllUsers.addAll(users);
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
