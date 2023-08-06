package com.example.postappwithkolin.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import com.example.postappwithkolin.Model.CommentInfo;
import com.example.postappwithkolin.Model.OnItemClickListener;
import com.example.postappwithkolin.R;
import com.example.postappwithkolin.SourceData.SAGDataFromDataBase;
import com.example.postappwithkolin.databinding.ActivityCommentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ktx.Firebase;
import com.squareup.picasso.Picasso;
import com.example.postappwithkolin.Model.recycleComment;

import java.util.ArrayList;
import java.util.Objects;

public class CommentActivity extends AppCompatActivity {

    ActivityCommentBinding binding;
    String userName, userPhoto , postImage, postComment , CommentText;
    int position ;
    SAGDataFromDataBase model = new SAGDataFromDataBase();

    ArrayList<CommentInfo> infos = new ArrayList<>();


    private static final String TAG = "CommentActivity";
    
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = new ViewModelProvider(this).get(SAGDataFromDataBase.class);

        mAuth = FirebaseAuth.getInstance();

        binding.commentFallback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Intent intent = getIntent();

        userName    = intent.getStringExtra("UserName");
        userPhoto   = intent.getStringExtra("UserPhoto");
        postImage   = intent.getStringExtra("postImage");
        postComment = intent.getStringExtra("postComment");
        position = intent.getIntExtra("position" , 0);


        Picasso.get().load(Uri.parse(userPhoto)).into(binding.customUserPhoto);
        Picasso.get().load(Uri.parse(postImage)).into(binding.customPostImage);
        binding.customUserName.setText(userName);
        binding.customPostComment.setText(postComment);
        Log.d(TAG, "onCreate: "+postImage);

        if(postImage.equals("null"))
            binding.customPostImage.setVisibility(View.GONE);


        binding.commentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentText = binding.CommentText.getText().toString();
                binding.CommentText.setText("");

                String CUserName = mAuth.getCurrentUser().getDisplayName();
                String CUserPhoto = mAuth.getCurrentUser().getPhotoUrl().toString();

                CommentInfo info = new CommentInfo(CUserName , CUserPhoto , CommentText);

                model.uploadCommentInformation(info , position);
            }
        });


        model.getAllComments(position);
        model.get_mutables().observe(this, new Observer<ArrayList<CommentInfo>>() {
            @Override
            public void onChanged(ArrayList<CommentInfo> commentInfos) {
                recycleComment rv = new recycleComment(commentInfos, new OnItemClickListener() {
                    @Override
                    public void onItemClick(int pos) {
                        CommentInfo inf = commentInfos.get(pos);
                        Intent in = new Intent(CommentActivity.this ,profileActivity.class );
                        in.putExtra("UserName", inf.getCommentUserName());
                        in.putExtra("UserPhoto", inf.getCommentUserPhoto());
                        startActivity(in);
                    }
                });
                binding.profilePosts.setAdapter(rv);
                binding.profilePosts.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
                binding.profilePosts.setHasFixedSize(true);
            }
        });

    }
}