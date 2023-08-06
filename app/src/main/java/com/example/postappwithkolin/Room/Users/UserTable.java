package com.example.postappwithkolin.Room.Users;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "userTable")
public class UserTable {
    @PrimaryKey(autoGenerate = true)
    int id;
    String UserName;
    String email;
    String UserPhoto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return UserName;
    }



    public String getEmail() {
        return email;
    }


    public String getUserPhoto() {
        return UserPhoto;
    }


    public UserTable(String UserName, String email, String UserPhoto) {
        this.UserName = UserName;
        this.email = email;
        this.UserPhoto = UserPhoto;
    }
}
