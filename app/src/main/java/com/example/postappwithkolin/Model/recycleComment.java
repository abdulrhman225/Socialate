package com.example.postappwithkolin.Model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.postappwithkolin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class recycleComment extends RecyclerView.Adapter<recycleComment.holder> {

    ArrayList<CommentInfo> infos;
    OnItemClickListener listener;

    public recycleComment(ArrayList<CommentInfo> infos , OnItemClickListener listener  ){
        this.infos = infos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_comment , parent , false);
        holder h = new holder(v);
        return h;
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        CommentInfo info = infos.get(position);
        Picasso.get().load(info.getCommentUserPhoto()).into(holder.iv_userPhoto);
        holder.tv_userName.setText(info.getCommentUserName());
        holder.tv_Comment.setText(info.getCommentComment());
        holder.tv_userName.setTag(position);
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    class holder extends RecyclerView.ViewHolder{
        CircleImageView iv_userPhoto;
        TextView tv_userName;
        TextView tv_Comment;
        public holder(@NonNull View itemView) {
            super(itemView);

            iv_userPhoto = itemView.findViewById(R.id.comment_CommentUserPhoto);
            tv_userName = itemView.findViewById(R.id.comment_CommentUserName);
            tv_Comment = itemView.findViewById(R.id.Comment_comment);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position =Integer.parseInt(tv_userName.getTag().toString());
                    listener.onItemClick(position);
                }
            });
        }
    }
}
