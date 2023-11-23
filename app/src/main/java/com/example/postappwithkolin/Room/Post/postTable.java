package com.example.postappwithkolin.Room.Post;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "post_table")
public class postTable {

    @PrimaryKey(autoGenerate = true)
    int postId;
    String userImage;
    String postImage;
    String userName;
    String postVideo;
    String postName;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getPostImage() {
        return postImage;
    }

    public String getUserName() {
        return userName;
    }

    public String getPostName() {
        return postName;
    }

    public String getPostVideo() {
        return postVideo;
    }

    public postTable(String userImage, String postImage, String postVideo , String userName, String postName ) {
        this.userImage = userImage;
        this.postImage = postImage;
        this.userName = userName;
        this.postName = postName;
        this.postVideo = postVideo;
    }
}
