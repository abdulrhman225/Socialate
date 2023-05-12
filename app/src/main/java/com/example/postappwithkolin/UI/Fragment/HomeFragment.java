package com.example.postappwithkolin.UI.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.postappwithkolin.Model.Post_recycler;
import com.example.postappwithkolin.Model.UserPost;
import com.example.postappwithkolin.SourceData.SAGDataFromDataBase;
import com.example.postappwithkolin.UI.MainActivity;
import com.example.postappwithkolin.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    FirebaseAuth mAuth;

    SAGDataFromDataBase model = new  SAGDataFromDataBase();
    public static ArrayList<UserPost>posts = new ArrayList<>();
    Post_recycler rv ;

    OnItemClickListener1 onItemClickListener1;
    onCommentClick commentClick;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onItemClickListener1 = (OnItemClickListener1) context;
        commentClick = (onCommentClick) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();

        FirebaseUser user = mAuth.getCurrentUser();
        binding.mainUserName.setText(user.getDisplayName());
        Picasso.get().load(user.getPhotoUrl()).into(binding.mainIVUserImage);

        putProfileInfo();

        model = new ViewModelProvider(HomeFragment.this).get(SAGDataFromDataBase.class);
        updateData();


        return v;
    }

    //put UserName and Profile Photo
    public void putProfileInfo() {
        if (MainActivity.Companion.getUserName() != null && MainActivity.Companion.getUserPhoto() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder().setDisplayName(MainActivity.Companion.getUserName())
                    .setPhotoUri(Uri.parse(MainActivity.Companion.getUserPhoto())).build();

            user.updateProfile(updateProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        binding.mainUserName.setText(user.getDisplayName());
                        Picasso.get().load(user.getPhotoUrl()).into(binding.mainIVUserImage);
                    }
                }
            });
        }
    }


    public void  updateData(){
        model.getPost();
        model.getMutable().observe(getViewLifecycleOwner(), new Observer<ArrayList<UserPost>>() {
            @Override
            public void onChanged(ArrayList<UserPost> userPosts) {
                posts.addAll(userPosts);
                rv = new  Post_recycler(userPosts, new Post_recycler.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        onItemClickListener1.onItemClick1(position);
                    }
                }, new Post_recycler.onCommentButtonClick() {
                    @Override
                    public void onCommentClick(int position) {
                        commentClick.onCommentclick(position);
                    }
                });
                binding.MainRecycler.setAdapter(rv);
                binding.MainRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.MainRecycler.setHasFixedSize(true);

            }
        });
    }

    public interface OnItemClickListener1{
        void onItemClick1(int position);
    }

    public interface onCommentClick{
        void onCommentclick(int position);
    }
}
